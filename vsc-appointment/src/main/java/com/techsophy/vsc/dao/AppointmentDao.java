package com.techsophy.vsc.dao;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.techsophy.vsc.utils.DBUtils;

import io.micrometer.core.instrument.Counter;

import io.micrometer.core.instrument.MeterRegistry;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;

@Repository
public class AppointmentDao {

	private final Logger logger = LoggerFactory.getLogger(AppointmentDao.class);

	@Qualifier("vsc")
	@Autowired
	DataSource vscDataSource;

	@Autowired
	DBUtils dbUtils;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private JdbcTemplate jdbcTemplate;

//	@Autowired
//	private CounterService counterService;
//
//	@Autowired
//	private GaugeService gaugeService;

	@ExceptionHandler
	void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value());
	}

	public String getUserRoles(String userId) {
		logger.info("in getUserRoles() to fetch roles for user - " + userId);
		Connection vscConn = null;
		Statement roleStmt = null;
		JSONObject response = new JSONObject();
		JSONArray arr = new JSONArray();
		JSONObject rolesObj = new JSONObject();
		String user = "";
		String serviceId = "";
		String serviceName = "";
		try {
			rolesObj.put("roles", "null");
			vscConn = vscDataSource.getConnection();
			roleStmt = vscConn.createStatement();
			String rolesQuery = "select ur.*,u.sc_id, sc.name from vsc_user_roles ur,vsc_users u, m_service_centers sc where ur.user_id=u.user_id and u.email='"
					+ userId + "' and sc.sc_id=u.sc_id";
			logger.info("in getUserRoles() and executing the query - " + rolesQuery);
			ResultSet rs = roleStmt.executeQuery(rolesQuery);
			while (rs.next()) {
				rolesObj = new JSONObject();
				rolesObj.put("role", rs.getString(2));
				user = rs.getString(1);
				serviceId = rs.getString(3);
				serviceName = rs.getString(4);
				arr.put(rolesObj);
			}
			response.put("userId", user);
			response.put("serviceId", serviceId);
			response.put("serviceName", serviceName);
			response.put("roles", arr);
		} catch (SQLException e) {
			logger.error("failed to fetch roles for user - " + userId + " due to - " + e);
			// e.printStackTrace();
		} catch (JSONException e) {
			logger.error("failed to fetch roles for user - " + userId + " due to - " + e);
			// e.printStackTrace();
		} finally {
			dbUtils.closeStatement(roleStmt);
			dbUtils.closeConnection(vscConn);
		}
		logger.info("response from getUserRoles() " + response.toString());
		return response.toString();
	}

	Counter successes = null;
	Counter failure = null;

	public String getAllCountries() {
		JSONObject response = new JSONObject();
		JSONArray arr = new JSONArray();
		JSONObject countryObj = new JSONObject();
		try {
			String countryQuery = "select * from m_countries";
			logger.info("in getAllCountries() and fetching the result for query - " + countryQuery);

			List<Map<String, Object>> countryLst = jdbcTemplate.queryForList(countryQuery);
			if (!countryLst.isEmpty()) {
				successes = Metrics.counter("getAllCountries", "result", "success");
				for (Map<String, Object> countryMap : countryLst) {
					countryObj = new JSONObject();
					countryObj.put("id", countryMap.get("country_id"));
					countryObj.put("name", countryMap.get("name"));
					arr.put(countryObj);

				}
			}
			response.put("countries", arr);

		} catch (DataAccessException e) {
			failure = Metrics.counter("getAllCountries", "result", "failure");
			failure.increment();
			logger.error("failed to fetch details from getAllCountries() due to - " + e);
		} catch (JSONException e) {
			failure = Metrics.counter("getAllCountries", "result", "failure");
			failure.increment();
			logger.error("failed to fetch details from getAllCountries() due to - " + e);
		}
		logger.info("response from getAllCountries() " + response.toString());
		return response.toString();
	}

	public String getVisaTypes(String countryId) {
		JSONObject response = new JSONObject();
		JSONArray arr = new JSONArray();
		JSONObject visaTypesObj = new JSONObject();
		try {
//			String categoryQuery = "select distinct(visa_type) from m_visa_types vt "
//					+ "left join m_service_centers sc " + "on  vt.sc_id = sc.sc_id "
//					+ "where sc.country_id in (select cn.country_id from m_countries cn where cn.country_id=?)";
//			
			String categoryQuery = "select visa_type_id,visa_type from m_visa_types vt where vt.visa_type_id in( select distinct(visa_type_id) from m_visa_fees vf left join m_service_centers sc on vf.country_id=sc.country_id where sc.country_id in (select cn.country_id from m_countries cn where cn.country_id=?))";

			List<Map<String, Object>> visaLst = jdbcTemplate.queryForList(categoryQuery, new Object[] { countryId });
			logger.info("in getVisaTypes() and fetching the result for query - " + categoryQuery);
			if (!visaLst.isEmpty()) {
				for (Map<String, Object> visaMap : visaLst) {
					visaTypesObj = new JSONObject();
					visaTypesObj.put("id", visaMap.get("visa_type_id"));
					visaTypesObj.put("name", visaMap.get("visa_type"));
					arr.put(visaTypesObj);
				}
			}
			response.put("visatypes", arr);
		} catch (DataAccessException e) {
			logger.error("failed to fetch details from getVisaTypes() due to - " + e);
		} catch (JSONException e) {
			logger.error("failed to fetch details from getVisaTypes() due to - " + e);
			// e.printStackTrace();
		}
		logger.info("response from getVisaTypes() " + response.toString());
		return response.toString();
	}

	public String getMaritalStatus() {
		JSONObject response = new JSONObject();
		JSONArray arr = new JSONArray();
		JSONObject msObj = new JSONObject();
		try {
			String maritalQuery = "select * from m_marital_status";
			logger.info("in getMaritalStatus() and fetching the result for query - " + maritalQuery);
			List<Map<String, Object>> marritalStatusLst = jdbcTemplate.queryForList(maritalQuery);
			if (!marritalStatusLst.isEmpty()) {
				for (Map<String, Object> marritalStatusMap : marritalStatusLst) {
					msObj = new JSONObject();
					msObj.put("name", marritalStatusMap.get("marital_status"));
					arr.put(msObj);
				}
			}
			response.put("maritalstatus", arr);
		} catch (DataAccessException e) {
			logger.error("failed to fetch details from getMaritalStatus() due to - " + e);
		} catch (JSONException e) {
			logger.error("failed to fetch details from getMaritalStatus() due to - " + e);
		}
		logger.info("response from getMaritalStatus() " + response.toString());
		return response.toString();
	}

	public String getReligion() {
		JSONObject response = new JSONObject();
		JSONArray arr = new JSONArray();
		JSONObject religionObj = new JSONObject();
		try {
			String religionQuery = "select * from m_religion";
			logger.info("in getReligion() and fetching the result for query - " + religionQuery);
			List<Map<String, Object>> religionLst = jdbcTemplate.queryForList(religionQuery);
			if (!religionLst.isEmpty()) {
				for (Map<String, Object> religionMap : religionLst) {
					religionObj = new JSONObject();
					religionObj.put("name", religionMap.get("religion"));
					arr.put(religionObj);
				}
			}
			response.put("religion", arr);
		} catch (DataAccessException e) {
			logger.error("failed to fetch details from getReligion() due to - " + e);
		} catch (JSONException e) {
			logger.error("failed to fetch details from getReligion() due to - " + e);
		}
		logger.info("response from getReligion() " + response.toString());
		return response.toString();
	}

	public String getModeOfEntry() {
		JSONObject response = new JSONObject();
		JSONArray arr = new JSONArray();
		JSONObject moeObj = new JSONObject();
		try {
			String modeofentryQuery = "select * from m_mode_of_entries";
			logger.info("in getModeOfEntry() and fetching the result for query - " + modeofentryQuery);
			List<Map<String, Object>> modeLst = jdbcTemplate.queryForList(modeofentryQuery);
			if (!modeLst.isEmpty()) {
				for (Map<String, Object> modeMap : modeLst) {
					moeObj = new JSONObject();
					moeObj.put("name", modeMap.get("mode_of_entry"));
					arr.put(moeObj);
				}
			}
			response.put("modeofentry", arr);
		} catch (DataAccessException e) {
			logger.error("failed to fetch details from getModeOfEntry() due to - " + e);
		} catch (JSONException e) {
			logger.error("failed to fetch details from getModeOfEntry() due to - " + e);
		}
		logger.info("response from getModeOfEntry() " + response.toString());
		return response.toString();
	}

	public String getPortOfEntries() {
		JSONObject response = new JSONObject();
		JSONArray arr = new JSONArray();
		JSONObject poeObj = new JSONObject();
		try {
			String portofentryQuery = "select * from m_port_of_entries";
			logger.info("in getPortOfEntries() and fetching the result for query - " + portofentryQuery);
			List<Map<String, Object>> portLst = jdbcTemplate.queryForList(portofentryQuery);
			if (!portLst.isEmpty()) {
				for (Map<String, Object> portMap : portLst) {
					poeObj = new JSONObject();
					poeObj.put("name", portMap.get("port_of_entry"));
					arr.put(poeObj);
				}
			}
			response.put("portofentries", arr);
		} catch (DataAccessException e) {
			logger.error("failed to fetch details from getPortOfEntries() due to - " + e);
		} catch (JSONException e) {
			logger.error("failed to fetch details from getPortOfEntries() due to - " + e);
		}
		logger.info("response from getPortOfEntries() " + response.toString());
		return response.toString();
	}

	public String getTypeOfEntries() {
		JSONObject response = new JSONObject();
		JSONArray arr = new JSONArray();
		JSONObject toeObj = new JSONObject();
		try {
			String typeofentryQuery = "select * from m_type_of_entries";
			logger.info("in getTypeOfEntries() and fetching the result for query - " + typeofentryQuery);
			List<Map<String, Object>> typeLst = jdbcTemplate.queryForList(typeofentryQuery);
			if (!typeLst.isEmpty()) {
				for (Map<String, Object> typeMap : typeLst) {
					toeObj = new JSONObject();
					toeObj.put("id", typeMap.get("toe_id"));
					toeObj.put("name", typeMap.get("type_of_entry"));
					arr.put(toeObj);
				}
			}
			response.put("typeofentries", arr);
		} catch (DataAccessException e) {
			logger.error("failed to fetch details from getTypeOfEntries() due to - " + e);
		} catch (JSONException e) {
			logger.error("failed to fetch details from getTypeOfEntries() due to - " + e);
		}
		logger.info("response from getTypeOfEntries() " + response.toString());
		return response.toString();
	}

	public String getPassportTypes(String countryId) {
		JSONObject response = new JSONObject();
		JSONArray arr = new JSONArray();
		JSONObject pstObj = new JSONObject();
		try {
			System.out.println(countryId + "counteryId");
			String pptQuery = "select pst.* from o_passport_types pst " + "left join m_countries cn "
					+ "on  pst.country_id = cn.country_id " + "where cn.country_id=?";
			logger.info("in getPassportTypes() and fetching the result for query - " + pptQuery);
			List<Map<String, Object>> ppLst = jdbcTemplate.queryForList(pptQuery, new Object[] { countryId });
			if (!ppLst.isEmpty()) {
				for (Map<String, Object> ppMap : ppLst) {
					pstObj = new JSONObject();
					pstObj.put("name", ppMap.get("pp_type_id"));
					arr.put(pstObj);
				}
			}
			response.put("passporttypes", arr);
		} catch (DataAccessException e) {
			logger.error("failed to fetch details from getPassportTypes() due to - " + e);
		} catch (JSONException e) {
			logger.error("failed to fetch details from getPassportTypes() due to - " + e);
		}
		logger.info("response from getPassportTypes() " + response.toString());
		return response.toString();
	}

	public String getCities(String countryId, String scId) {
		JSONObject response = new JSONObject();
		JSONArray arr = new JSONArray();
		JSONObject pstObj = new JSONObject();
		try {
			/*
			 * String citiQuery = "select city.* from vps_city city " +
			 * "left join m_countries cn " + "on  city.country_id = cn.country_id " +
			 * "where cn.country_id='" + countryId + "'";
			 */
			String missionQuery = "select * from vps.m_service_centers "
					+ "where sc_id=(select parent_id from m_service_centers "
					+ "where sc_id=(select parent_id from m_service_centers where sc_id=? and country_id=?));";
			logger.info("in getCities() and fetching the result for query - " + missionQuery);

			List<Map<String, Object>> cityLst = jdbcTemplate.queryForList(missionQuery,
					new Object[] { scId, countryId });
			if (!cityLst.isEmpty()) {
				for (Map<String, Object> cityMap : cityLst) {
					pstObj = new JSONObject();
					pstObj.put("name", cityMap.get("name"));
					arr.put(pstObj);
				}
			}
			response.put("cities", arr);
		} catch (DataAccessException e) {
			logger.error("failed to fetch details from getCities() due to - " + e);
		} catch (JSONException e) {
			logger.error("failed to fetch details from getCities() due to - " + e);
		}
		logger.info("response from getCities() " + response.toString());
		return response.toString();
	}

	public String getGender() {
		JSONObject response = new JSONObject();
		JSONArray arr = new JSONArray();
		JSONObject genderObj = new JSONObject();
		try {
			String query = "select * from m_gender";
			logger.info("in getGender() and fetching the result for query - " + query);
			List<Map<String, Object>> genderLst = jdbcTemplate.queryForList(query);
			if (!genderLst.isEmpty()) {
				for (Map<String, Object> genderMap : genderLst) {
					genderObj = new JSONObject();
					genderObj.put("name", genderMap.get("type"));
					arr.put(genderObj);
				}
			}
			response.put("gender", arr);
		} catch (DataAccessException e) {
			logger.error("failed to fetch details from getGender() due to - " + e);
		} catch (JSONException e) {
			logger.error("failed to fetch details from getGender() due to - " + e);
		}
		logger.info("response from getGender() " + response.toString());
		return response.toString();
	}

	public String getVisaFeeDetails(String visaType, String noOfVisits, String countryId) {
		JSONObject response = new JSONObject();
		JSONObject visaFeeObj = new JSONObject();
		try {
			response.put("data", "null");
			String visaFeeQuery = "select visa_fee,enjazit_fee,service_fee,insurance_fee,insuranceit_fee,medical_fee from m_visa_fees where visa_type_id=? and country_id=? and toe_id=?";
			logger.info("in getVisaFeeDetails() and fetching the result for query - " + visaFeeQuery);
			List<Map<String, Object>> visaFeeLst = jdbcTemplate.queryForList(visaFeeQuery,
					new Object[] { visaType, countryId, noOfVisits });
			if (!visaFeeLst.isEmpty()) {
				for (Map<String, Object> visaFeeMap : visaFeeLst) {
					int visaFee = ((BigDecimal) visaFeeMap.get("visa_fee")).intValue();
					int enjazItFee = ((BigDecimal) visaFeeMap.get("enjazit_fee")).intValue();
					int serviceFee = ((BigDecimal) visaFeeMap.get("service_fee")).intValue();
					int insuranceFee = ((BigDecimal) visaFeeMap.get("insurance_fee")).intValue();
					int insuranceItFee = ((BigDecimal) visaFeeMap.get("insuranceit_fee")).intValue();
					int medicalFee = ((BigDecimal) visaFeeMap.get("medical_fee")).intValue();
					int totalFee = visaFee + enjazItFee + serviceFee + insuranceFee + insuranceItFee + medicalFee;
					visaFeeObj.put("visa_fee", visaFee);
					visaFeeObj.put("enjazit_fee", enjazItFee);
					visaFeeObj.put("service_fee", serviceFee);
					visaFeeObj.put("insurance_fee", insuranceFee);
					visaFeeObj.put("insuranceit_fee", insuranceItFee);
					visaFeeObj.put("medical_fee", medicalFee);
					visaFeeObj.put("total_fee", totalFee);
				}
			} else {
				visaFeeQuery = "select visa_fee,enjazit_fee,service_fee,insurance_fee,insuranceit_fee,medical_fee from m_visa_fees where visa_type_id=? and country_id=? and toe_id='ALL'";
				logger.info("in getVisaFeeDetails() and fetching the result for query - " + visaFeeQuery);
				List<Map<String, Object>> allVisaFeeLst = jdbcTemplate.queryForList(visaFeeQuery,
						new Object[] { visaType, countryId });
				if (!allVisaFeeLst.isEmpty()) {
					for (Map<String, Object> visaFeeMap : allVisaFeeLst) {
						int visaFee = ((BigDecimal) visaFeeMap.get("visa_fee")).intValue();
						int enjazItFee = ((BigDecimal) visaFeeMap.get("enjazit_fee")).intValue();
						int serviceFee = ((BigDecimal) visaFeeMap.get("service_fee")).intValue();
						int insuranceFee = ((BigDecimal) visaFeeMap.get("insurance_fee")).intValue();
						int insuranceItFee = ((BigDecimal) visaFeeMap.get("insuranceit_fee")).intValue();
						int medicalFee = ((BigDecimal) visaFeeMap.get("medical_fee")).intValue();
						int totalFee = visaFee + enjazItFee + serviceFee + insuranceFee + insuranceItFee + medicalFee;
						visaFeeObj.put("visa_fee", visaFee);
						visaFeeObj.put("enjazit_fee", enjazItFee);
						visaFeeObj.put("service_fee", serviceFee);
						visaFeeObj.put("insurance_fee", insuranceFee);
						visaFeeObj.put("insuranceit_fee", insuranceItFee);
						visaFeeObj.put("medical_fee", medicalFee);
						visaFeeObj.put("total_fee", totalFee);
					}
				}
			}
			response.put("data", visaFeeObj);
		} catch (DataAccessException e) {
			logger.error("failed to fetch details from getVisaFeeDetails() due to - " + e);
		} catch (JSONException e) {
			logger.error("failed to fetch details from getVisaFeeDetails() due to - " + e);
		}
		logger.info("response from getVisaFeeDetails() " + response.toString());
		return response.toString();
	}

	public String getAvailableTimeSlots(String serviceId, String startDay, String endDay) {
		HashMap<String, JSONObject> timeSlotMap = new HashMap<String, JSONObject>();
		HashMap<String, JSONObject> timeSlotConfigMap = new HashMap<String, JSONObject>();
		String response = "";
		JSONObject tsObj = new JSONObject();
		JSONObject tsConfigObj = new JSONObject();
		try {
			String timeSlotQuery = "select day,slot_no,scheduled_normal_visits,scheduled_priority_visits from o_time_slots where sc_id=? order by day,slot_no asc";
			logger.info("in getAvailableTimeSlots() and fetching the result for query - " + timeSlotQuery);

			String key = null;
			List<Map<String, Object>> timeSlotRowsLst = jdbcTemplate.queryForList(timeSlotQuery,
					new Object[] { serviceId });
			if (!timeSlotRowsLst.isEmpty()) {
				for (Map<String, Object> timeSlotRowMap : timeSlotRowsLst) {
					tsObj = new JSONObject();
					tsObj.put("slot_no", timeSlotRowMap.get("slot_no"));
					tsObj.put("day", timeSlotRowMap.get("day"));
					key = timeSlotRowMap.get("slot_no") + ":" + timeSlotRowMap.get("day");
					tsObj.put("scheduled_normal_visits", timeSlotRowMap.get("scheduled_normal_visits"));
					tsObj.put("scheduled_priority_visits", timeSlotRowMap.get("scheduled_priority_visits"));
					timeSlotMap.put(key, tsObj);
				}
				String timConfigQuery = "select sc_id,slot_no,start_time,end_time,max_normal_visits,max_priority_visits from m_time_slot_configuration where sc_id=?";
				logger.info("in getAvailableTimeSlots() and fetching the result for query - " + timConfigQuery);
				List<Map<String, Object>> timeConfigsLst = jdbcTemplate.queryForList(timConfigQuery,
						new Object[] { serviceId });
				for (Map<String, Object> timeConfigsMap : timeConfigsLst) {
					key = ((Integer) timeConfigsMap.get("slot_no")).toString();
					tsConfigObj = new JSONObject();
					tsConfigObj.put("slot_no", timeConfigsMap.get("slot_no"));
					tsConfigObj.put("start_time", timeConfigsMap.get("start_time"));
					tsConfigObj.put("max_normal_visits", timeConfigsMap.get("max_normal_visits"));
					tsConfigObj.put("max_priority_visits", timeConfigsMap.get("max_priority_visits"));
					timeSlotConfigMap.put(key, tsConfigObj);
				}
			}
			response = returnTimeSlotJson(timeSlotMap, timeSlotConfigMap, startDay, endDay);
		} catch (DataAccessException e) {
			logger.error("failed to fetch details from getAvailableTimeSlots() due to - " + e);

		} catch (JSONException e) {
			logger.error("failed to fetch details from getAvailableTimeSlots() due to - " + e);

		}
		logger.info("response from getAvailableTimeSlots() " + response.toString());
		return response;
	}

	public String returnTimeSlotJson(HashMap<String, JSONObject> tsMap, HashMap<String, JSONObject> tsConfigMap,
			String startDay, String endDay) {
		Set<Entry<String, JSONObject>> config = tsConfigMap.entrySet();
		SimpleDateFormat dateOnly = new SimpleDateFormat("yyyy-MM-dd");
		GregorianCalendar cal = new GregorianCalendar();
		Date startDate = new Date();
		Date endDate = null;
		int diffInDays = 0;
		JSONObject finalTsObj = new JSONObject();
		JSONObject finalTsConfigObj = new JSONObject();
		JSONArray arr = new JSONArray();
		JSONArray days = new JSONArray();

		try {
			if (startDay != null) {
				startDate = dateOnly.parse(startDay);
			}
			if (endDay != null) {
				endDate = dateOnly.parse(endDay);
			}
			if (endDate != null) {
				diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
			}
			if (diffInDays == 0) {
				diffInDays = 30;
			}
			cal.setTime(startDate);

			JSONObject finalDaysObj;
			// for each day
			for (int i = 0; i < diffInDays; i++) {
				finalDaysObj = new JSONObject();
				finalDaysObj.put("day", dateOnly.format(cal.getTime()));

				// for each entry of service center
				for (Entry<String, JSONObject> configEntry : config) {
					JSONObject tsConfigValObj = configEntry.getValue();

					JSONObject tsValObj = tsMap
							.get(dateOnly.format(cal.getTime()) + ":" + tsConfigValObj.get("slot_no"));

					int scheduledNormalVisits = 0, scheduledPriorityVisits = 0;
					int maxPriorityVisits = Integer.parseInt(tsConfigValObj.get("max_priority_visits").toString());
					int maxNormalVisits = Integer.parseInt(tsConfigValObj.get("max_normal_visits").toString());

					if (tsValObj != null) {
						// had earlier scheduled appointments, so subtract them
						scheduledNormalVisits = Integer.parseInt(tsValObj.get("scheduled_normal_visits").toString());
						scheduledPriorityVisits = Integer
								.parseInt(tsValObj.get("scheduled_priority_visits").toString());

					}
					finalTsObj = new JSONObject();
					finalTsObj.put("slot_no", tsConfigValObj.get("slot_no"));
					String startTimeVal = tsConfigValObj.get("start_time").toString();
					int startTime = Integer.parseInt(startTimeVal.substring(0, 2));
					if (startTime > 11) {
						if (startTime >= 13) {
							startTime = startTime - 12;
							if (startTime > 9) {
								finalTsObj.put("start_time",
										startTime + startTimeVal.substring(2, startTimeVal.length() - 3) + " PM");
							} else {
								finalTsObj.put("start_time",
										"0" + startTime + startTimeVal.substring(2, startTimeVal.length() - 3) + " PM");
							}
						} else {
							finalTsObj.put("start_time", startTimeVal.substring(0, startTimeVal.length() - 3) + " PM");
						}
					} else {
						finalTsObj.put("start_time", startTimeVal.substring(0, startTimeVal.length() - 3) + " AM");
					}
					finalTsObj.put("avl_normal_visits", maxNormalVisits - scheduledNormalVisits);
					finalTsObj.put("avl_priority_visits", maxPriorityVisits - scheduledPriorityVisits);
					arr.put(finalTsObj);
				}
				finalDaysObj.put("slots", arr);
				days.put(finalDaysObj);
				cal.add(Calendar.DATE, 1);
				arr = new JSONArray();
			}
			finalTsConfigObj.put("days", days);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return finalTsConfigObj.toString();
	}

	int status;

	public void increaseStatusCodeCount(int status) {
		this.status = status;

	}

	MeterRegistry registry = new CompositeMeterRegistry();
	private Timer timer;

	public String createAppointment(String input) {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus txStatus = transactionManager.getTransaction(def);
		JSONObject response = new JSONObject();

		long start = System.currentTimeMillis();
		this.timer = registry.timer("app.timer", "type", "ping");
		System.out.println(status);

//		Timer timer = registry.timer("app.event");
		timer.record(() -> {
			try {

				response.put("app_ref_id", "null");
				String appointmentId = dbUtils.generateId().toUpperCase();
				String visaAppointmentQuery = dbUtils.createVisaAppointmentQuery(input, appointmentId);
				String visaTimeSlotQuery = dbUtils.visaTimeSlotQuery(input);
				String visaApplicationQuery = dbUtils.createVisaApplicationQuery(input, appointmentId);
				String[] queryArr = { visaTimeSlotQuery, visaAppointmentQuery, visaApplicationQuery };
				int[] affectedRows = jdbcTemplate.batchUpdate(queryArr);
				if (affectedRows.length >= 3) {
					Metrics.counter("vsc.appointments.created").increment(1.0);
					Metrics.counter("vsc.appointments.created", "result", "success");
					response.put("app_ref_id", appointmentId.toUpperCase());
				}

//			vscConn = vscDataSource.getConnection();
//			stmt = vscConn.createStatement();
//			vscConn.setAutoCommit(false);
//			stmt.addBatch(visaTimeSlotQuery);
//			stmt.addBatch(visaAppointmentQuery);
//			stmt.addBatch(visaApplicationQuery);
//			int[] affectedRows = stmt.executeBatch();
//			if (affectedRows.length >= 3) {
//				response.put("app_ref_id", appointmentId.toUpperCase());
//			}
				timer.record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
//			vscConn.commit();
				transactionManager.commit(txStatus);
			} catch (DataAccessException e) {
				Metrics.counter("vsc.appointment.exception", "message", e.toString());
				Metrics.counter("vsc.appointment.exception").increment(1.0);
				logger.error("failed to fetch details from createAppointment() due to - " + e);
				transactionManager.rollback(txStatus);

			} catch (JSONException e) {
				Metrics.counter("vsc.appointment.exception", "message", e.toString());
				Metrics.counter("vsc.appointment.exception").increment(1.0);
				transactionManager.rollback(txStatus);

				logger.error("failed to fetch details from createAppointment() due to - " + e);
				e.printStackTrace();
			}
		});

//		registry.timer("vsc.appointment.timerecord","timer");
		logger.info("response from createAppointment() " + response.toString());
		return response.toString();
	}

	public String getAppointmentDetails(String appRefId, String serviceId) {
		logger.info("in getAppointmentDetails() to fetch appointment details for ref id - " + appRefId
				+ " and service id - " + serviceId);
		JSONObject response = new JSONObject();
		JSONObject apptObj = new JSONObject();
		try {
			String apptQuery = "select appt.app_date,appt.slot_no,appt.app_type,vt.visa_type,appl.first_name,appl.passport_no,tsc.start_time,appt.sc_id,appt.no_applicants "
					+ "from visa_appointment appt " + "left join visa_application appl " + "on appt.app_id=appl.app_id "
					+ "left join m_time_slot_configuration tsc " + "on appt.sc_id=tsc.sc_id "
					+ " inner join m_visa_types vt on vt.visa_type_id=appt.visa_type_id where appt.slot_no=tsc.slot_no "
					+ "and appt.app_id=? and appt.sc_id=?";

			logger.info("in getAppointmentDetails() and executing the query - " + apptQuery);
			List<Map<String, Object>> appointmentDetailsLst = jdbcTemplate.queryForList(apptQuery,
					new Object[] { appRefId, serviceId });
			if (!appointmentDetailsLst.isEmpty()) {
				for (Map<String, Object> appointmentDetailsMap : appointmentDetailsLst) {
					apptObj.put("app_date", appointmentDetailsMap.get("app_date"));
					apptObj.put("slot-no", appointmentDetailsMap.get("slot_no"));
					apptObj.put("app_type", appointmentDetailsMap.get("app_type"));
					apptObj.put("visa_type", appointmentDetailsMap.get("visa_type"));
					apptObj.put("first_name", appointmentDetailsMap.get("first_name"));
					apptObj.put("passport_no", appointmentDetailsMap.get("passport_no"));
					int appTime = Integer.parseInt(appointmentDetailsMap.get("start_time").toString().substring(0, 2));
					String time = appointmentDetailsMap.get("start_time").toString();
					if (appTime > 11) {
						if (appTime >= 13) {
							appTime = appTime - 12;
							if (appTime > 9) {
								time = time.substring(2, time.length() - 3) + " PM";
							} else {
								time = "0" + appTime + time.substring(2, time.length() - 3) + " PM";
							}
						} else {
							time = time.substring(0, time.length() - 3) + " PM";
						}
					} else {
						time = time.substring(0, time.length() - 3) + " AM";
					}
					apptObj.put("app_time", time);
					apptObj.put("sc_id", appointmentDetailsMap.get("sc_id"));
					apptObj.put("no_of_applicants", appointmentDetailsMap.get("no_of_applicants"));
				}
			}
			response.put("apptdetails", apptObj);
		} catch (DataAccessException e) {
			logger.error("failed to fetch appointment details for ref id - " + appRefId + " service id - " + serviceId
					+ " due to - " + e);
			// e.printStackTrace();
		} catch (JSONException e) {
			logger.error("failed to fetch appointment details for ref id - " + appRefId + " service id - " + serviceId
					+ " due to - " + e);
			// e.printStackTrace();
		}
		logger.info("response from getAppointmentDetails() " + response.toString());
		return response.toString();
	}

	public String getTrackDetailsByAppRef(String appRefId) {
		Connection vscConn = null;
		Statement trackDetailsStmt = null;
		String trackDetailsQuery;
		JSONObject response = new JSONObject();
		JSONObject trackObj = new JSONObject();
		JSONArray trackArr = new JSONArray();
		try {
			response.put("track_details", new JSONArray());
			vscConn = vscDataSource.getConnection();
			trackDetailsStmt = vscConn.createStatement();
			/*
			 * trackDetailsQuery =
			 * "SELECT vt.security_tag,vt.action_time FROM visa_tracker vt, visa_application va where vt.e_number=va.e_number and vt.action='packaged4mission' and va.sc_id='"
			 * + serviceId + "' group by vt.security_tag,vt.action_time";
			 */
			trackDetailsQuery = "select vt.message, vu.user_name, vt.action_time from visa_tracker vt left join vsc_users vu on "
					+ "vu.email=vt.vsc_user_id where e_number = (select e_number from visa_application where app_id='"
					+ appRefId + "') " + "order by action_time asc";
			ResultSet rs = trackDetailsStmt.executeQuery(trackDetailsQuery);
			while (rs.next()) {
				trackObj = new JSONObject();
				trackObj.put("status", rs.getString(1));
				trackObj.put("uname", rs.getString(2));
				trackObj.put("time", rs.getString(3));
				trackArr.put(trackObj);
			}
			response.put("track_details", trackArr);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dbUtils.closeStatement(trackDetailsStmt);
			dbUtils.closeConnection(vscConn);
		}
		return response.toString();
	}

	public String getRescheduleAppDetails(String appRefId, String serviceId) {
		JSONObject response = new JSONObject();
		JSONObject apptObj = new JSONObject();
		try {
			String apptQuery = "select appt.app_date,appt.slot_no,appt.app_type,vt.visa_type,appl.first_name,appl.passport_no,tsc.start_time,appt.sc_id,appt.no_applicants "
					+ "from visa_appointment appt " + "left join visa_application appl " + "on appt.app_id=appl.app_id "
					+ "left join m_time_slot_configuration tsc " + "on appt.sc_id=tsc.sc_id "
					+ " inner join m_visa_types vt on vt.visa_type_id=appt.visa_type_id where appt.slot_no=tsc.slot_no "
					+ "and appt.app_id=? and appt.sc_id=?";
			logger.info("in getRescheduleAppDetails() and executing the query - " + apptQuery);
			List<Map<String, Object>> appointmentDetailsLst = jdbcTemplate.queryForList(apptQuery,
					new Object[] { appRefId, serviceId });
			if (!appointmentDetailsLst.isEmpty()) {
				for (Map<String, Object> appointmentDetailsMap : appointmentDetailsLst) {
					apptObj.put("app_date", appointmentDetailsMap.get("app_date"));
					apptObj.put("slot-no", appointmentDetailsMap.get("slot_no"));
					apptObj.put("app_type", appointmentDetailsMap.get("app_type"));
					apptObj.put("visa_type", appointmentDetailsMap.get("visa_type"));
					apptObj.put("first_name", appointmentDetailsMap.get("first_name"));
					apptObj.put("passport_no", appointmentDetailsMap.get("passport_no"));
					int appTime = Integer.parseInt(appointmentDetailsMap.get("start_time").toString().substring(0, 2));
					String time = appointmentDetailsMap.get("start_time").toString();
					if (appTime > 11) {
						if (appTime >= 13) {
							appTime = appTime - 12;
							if (appTime > 9) {
								time = time.substring(2, time.length() - 3) + " PM";
							} else {
								time = "0" + appTime + time.substring(2, time.length() - 3) + " PM";
							}
						} else {
							time = time.substring(0, time.length() - 3) + " PM";
						}
					} else {
						time = time.substring(0, time.length() - 3) + " AM";
					}
					apptObj.put("app_time", time);
					apptObj.put("sc_id", appointmentDetailsMap.get("sc_id"));
					apptObj.put("no_of_applicants", appointmentDetailsMap.get("no_of_applicants"));
				}
			}
			response.put("apptdetails", apptObj);
		} catch (DataAccessException e) {
			logger.error("failed to fetch reschedule appointment details for ref id - " + appRefId + " service id - "
					+ serviceId + " due to - " + e);
		} catch (JSONException e) {
			logger.error("failed to fetch reschedule appointment details for ref id - " + appRefId + " service id - "
					+ serviceId + " due to - " + e);
		}
		return response.toString();

	}
}
