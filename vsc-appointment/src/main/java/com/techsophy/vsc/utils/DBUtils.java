package com.techsophy.vsc.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.UUID;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DBUtils {

	private final Logger logger = LoggerFactory.getLogger(DBUtils.class);

	public String createUserQuery(String user, String id) {
		String userQuery = null;
		try {
			JSONObject userObj = new JSONObject(user);
			String userId = userObj.opt("userId") != null ? "'" + userObj.getString("userId") + "'" : null;
			String email = userObj.opt("email") != null ? "'" + userObj.getString("email") + "'" : null;
			String password = userObj.opt("password") != null ? "'" + userObj.getString("password") + "'" : null;

			userQuery = "insert into vsc_users (id, user_id, password, email,status) values ('" + id + "'," + userId
					+ "," + password + "," + email + "," + "'Active'" + ")";

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return userQuery;
	}

	public String generateId() {
		String uid = UUID.randomUUID().toString();
		uid = uid.substring(0, uid.indexOf("-"));
		return uid;
	}

	public void closeResultSet(ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
		} catch (Exception ex) {
		}
	}

	public void closeStatement(Statement st) {
		try {
			if (st != null)
				st.close();
		} catch (Exception ex) {
		}
	}

	public void closeConnection(Connection con) {
		try {
			if (con != null)
				con.close();
		} catch (Exception ex) {
		}
	}

	public String createVisaAppointmentQuery(String input, String appId) {
		String appointmentQuery = null;
		try {
			logger.info("in createVisaAppointmentQuery() for ref id - " + appId);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			GregorianCalendar cal = new GregorianCalendar();
			String appCreateDate = sdf.format(cal.getTime());

			JSONObject appointmentObj = new JSONObject(input);
			String scId = (appointmentObj.opt("scId") != null && !"".equals(appointmentObj.opt("scId")))
					? "'" + appointmentObj.getString("scId") + "'"
					: null;
			java.sql.Date appDate = null;
			if (appointmentObj.opt("appDate") != null && !"".equals(appointmentObj.opt("appDate"))) {
				SimpleDateFormat app_date = new SimpleDateFormat("yyyy-MM-dd");
				java.util.Date date = app_date.parse(appointmentObj.getString("appDate"));
				appDate = new java.sql.Date(date.getTime());
			}
			String applicationDate = "'" + appDate + "'";
			String slotNo = (appointmentObj.opt("slotNo") != null && !"".equals(appointmentObj.opt("slotNo")))
					? appointmentObj.getString("slotNo")
					: null;
			String visaTypeId = (appointmentObj.opt("visaType") != null
					&& !"".equals(appointmentObj.opt("visaType")))
							? "'" + appointmentObj.getString("visaType") + "'"
							: null;
			String totalVisaFee = (appointmentObj.opt("totalVisaFee") != null
					&& !"".equals(appointmentObj.opt("totalVisaFee"))) ? appointmentObj.getString("totalVisaFee")
							: null;
			String paymentType = (appointmentObj.opt("paymentType") != null
					&& !"".equals(appointmentObj.opt("paymentType")))
							? "'" + appointmentObj.getString("paymentType") + "'"
							: null;
			String paymentReference = (appointmentObj.opt("paymentReference") != null
					&& !"".equals(appointmentObj.opt("paymentReference")))
							? "'" + appointmentObj.getString("paymentReference") + "'"
							: null;
			String visaType = (appointmentObj.opt("appType") != null && !"".equals(appointmentObj.opt("appType")))
					? "'" + appointmentObj.getString("appType") + "'"
					: null;

			int noOfApplicants = (appointmentObj.opt("noOfApplicants") != null
					&& !"".equals(appointmentObj.opt("noOfApplicants")))
							? Integer.parseInt(appointmentObj.getString("noOfApplicants"))
							: 1;
			String applyFor = (appointmentObj.opt("applyFor") != null && !"".equals(appointmentObj.opt("applyFor")))
					? "'" + appointmentObj.getString("applyFor") + "'"
					: "Individual";

			String applicantMail = (appointmentObj.opt("applEmail") != null
					&& !"".equals(appointmentObj.opt("applEmail"))) ? "'" + appointmentObj.getString("applEmail") + "'"
							: null;
			String typeOfEntry = appointmentObj.opt("typeOfEntry") != null ? "'" + appointmentObj.getString("typeOfEntry") + "'"
					: null;
			appointmentQuery = "insert into visa_appointment (app_id, sc_id, app_date,slot_no,visa_type_id,type_of_entry,total_visa_fee,"
					+ "payment_type,payment_reference,app_type,app_status,created_date,no_applicants,applying_for,applicant_email) values ('"
					+ appId + "'," + scId + "," + applicationDate + "," + slotNo + "," + visaTypeId + "," + typeOfEntry + "," + totalVisaFee
					+ "," + paymentType + "," + paymentReference + "," + visaType + ",'created','" + appCreateDate
					+ "'," + noOfApplicants + ",'" + applyFor + "'," + applicantMail + ")";

			logger.info("in createVisaAppointmentQuery() for ref id - " + appId + " to prepare query - "
					+ appointmentQuery);

		} catch (JSONException e) {
			logger.error("failed to prepare createVisaAppointmentQuery due to - " + e);
			// e.printStackTrace();
		} catch (ParseException e) {
			logger.error("failed to prepare createVisaAppointmentQuery due to - " + e);
			// e.printStackTrace();
		}
		return appointmentQuery;
	}

	public String visaTimeSlotQuery(String input) {
		String timeSlotQuery = null;
		try {
			logger.info("in visaTimeSlotQuery()");
			JSONObject appointmentObj = new JSONObject(input);
			String scId = (appointmentObj.opt("scId") != null && !"".equals(appointmentObj.opt("scId")))
					? "'" + appointmentObj.getString("scId") + "'"
					: null;
			java.sql.Date appDate = null;
			if (appointmentObj.opt("appDate") != null && !"".equals(appointmentObj.opt("appDate"))) {
				SimpleDateFormat app_date = new SimpleDateFormat("yyyy-MM-dd");
				java.util.Date date = app_date.parse(appointmentObj.getString("appDate"));
				appDate = new java.sql.Date(date.getTime());
			}
			String applicationDate = "'" + appDate + "'";
			String slotNo = (appointmentObj.opt("slotNo") != null && !"".equals(appointmentObj.opt("slotNo")))
					? appointmentObj.getString("slotNo")
					: null;

			timeSlotQuery = "insert into o_time_slots (sc_id,slot_no,day,scheduled_normal_visits,"
					+ "scheduled_priority_visits) values (" + scId + "," + slotNo + "," + applicationDate
					+ ",1,1) ON DUPLICATE KEY UPDATE scheduled_normal_visits =(scheduled_normal_visits+1),scheduled_priority_visits=(scheduled_priority_visits+1)";

			logger.info("in visaTimeSlotQuery() to prepare query - " + timeSlotQuery);

		} catch (JSONException e) {
			logger.error("failed to prepare visaTimeSlotQuery due to - " + e);
			// e.printStackTrace();
		} catch (ParseException e) {
			logger.error("failed to prepare visaTimeSlotQuery due to - " + e);
			// e.printStackTrace();
		}
		return timeSlotQuery;
	}

	public String createVisaApplicationQuery(String input, String appId) {
		String createQuery = null;
		try {
			logger.info("in createVisaApplicationQuery() for ref id - " + appId);
			JSONObject createObj = new JSONObject(input);
			String scId = createObj.opt("scId") != null ? "'" + createObj.getString("scId") + "'" : null;
			String passportNo = createObj.opt("passportNo") != null ? "'" + createObj.getString("passportNo") + "'"
					: null;
			String ppTypeId = createObj.opt("passportType") != null ? "'" + createObj.getString("passportType") + "'"
					: null;
			java.sql.Date dateIssued = null;
			if (createObj.opt("dateIssued") != null) {
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
				java.util.Date date = sdf1.parse(createObj.getString("dateIssued"));
				dateIssued = new java.sql.Date(date.getTime());
			}
			String dateOfIssue = "'" + dateIssued + "'";

			java.sql.Date dateExpiry = null;
			if (createObj.opt("dateExpiry") != null) {
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
				java.util.Date date = sdf1.parse(createObj.getString("dateExpiry"));
				dateExpiry = new java.sql.Date(date.getTime());
			}
			String dateOfExpiry = "'" + dateExpiry + "'";
			String placeOfIssue = createObj.opt("placeOfIssue") != null
					? "'" + createObj.getString("placeOfIssue") + "'"
					: null;
			java.sql.Date dob = null;
			if (createObj.opt("dob") != null) {
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
				java.util.Date date = sdf1.parse(createObj.getString("dob"));
				dob = new java.sql.Date(date.getTime());
			}
			String dateOfBirth = "'" + dob + "'";
			String birthPlace = createObj.opt("birthPlace") != null ? "'" + createObj.getString("birthPlace") + "'"
					: null;
			String sponserId = createObj.opt("sponsorId") != null ? "'" + createObj.getString("sponsorId") + "'" : null;
			String invitationNo = createObj.opt("invitationNo") != null
					? "'" + createObj.getString("invitationNo") + "'"
					: null;
			String portOfEntry = createObj.opt("portOfEntry") != null ? "'" + createObj.getString("portOfEntry") + "'"
					: null;

			String modeOfEntry = createObj.opt("modeOfEntry") != null ? "'" + createObj.getString("modeOfEntry") + "'"
					: null;
			String firstName = createObj.getString("firstName") != null ? "'" + createObj.getString("firstName") + "'"
					: null;
			String lastName = createObj.getString("lastName") != null ? "'" + createObj.getString("lastName") + "'"
					: null;
			String fatherName = createObj.getString("fatherName") != null
					? "'" + createObj.getString("fatherName") + "'"
					: null;
			String gender = createObj.opt("gender") != null ? "'" + createObj.getString("gender") + "'" : null;
			String maritalSatus = createObj.getString("maritalSatus") != null
					? "'" + createObj.getString("maritalSatus") + "'"
					: null;
			String religion = createObj.opt("religion") != null ? "'" + createObj.getString("religion") + "'" : null;
			String qualification = (createObj.opt("qualification") != null
					&& !"".equals(createObj.opt("qualification"))) ? "'" + createObj.getString("qualification") + "'"
							: null;
			String occupation = createObj.opt("occupation") != null ? "'" + createObj.getString("occupation") + "'"
					: null;
			String email = createObj.opt("email") != null ? "'" + createObj.getString("email") + "'" : null;
			String mobile = createObj.opt("mobile") != null ? "'" + createObj.getString("mobile") + "'" : null;
			String address1 = createObj.opt("address1") != null ? "'" + createObj.getString("address1") + "'" : null;
			String address2 = createObj.opt("address2") != null ? "'" + createObj.getString("address2") + "'" : null;
			String address3 = createObj.opt("address3") != null ? "'" + createObj.getString("address3") + "'" : null;

			String addressCity = createObj.opt("addressCity") != null ? "'" + createObj.getString("addressCity") + "'"
					: null;
			String nationality = createObj.opt("nationality") != null ? "'" + createObj.getString("nationality") + "'"
					: null;
			String invitationOrPreapproved = createObj.opt("inviteOrPreapproved") != null
					? "'" + createObj.getString("inviteOrPreapproved") + "'"
					: null;

			createQuery = "insert into visa_application (sc_id, app_id, passport_no, pp_type_id, date_of_issue,"
					+ " date_of_expiry, place_of_issue, date_of_birth, place_of_birth, sponsor_id,"
					+ " invitation_no, port_of_entry,mode_of_entry, first_name,"
					+ " last_name, gender, marital_status, religion, qualification, occupation,"
					+ " email, mobile_no, address_1, address_2, address_3, address_city,"
					+ " nationality, invitation_or_preapproved, father_name) values (" + scId + ",'" + appId + "',"
					+ passportNo + "," + ppTypeId + "," + dateOfIssue + "," + dateOfExpiry + "," + placeOfIssue + ","
					+ dateOfBirth + "," + birthPlace + "," + sponserId + "," + invitationNo + "," + portOfEntry + ","
					+ modeOfEntry + "," + firstName + "," + lastName + "," + gender + "," + maritalSatus + ","
					+ religion + "," + qualification + "," + occupation + "," + email + "," + mobile + "," + address1
					+ "," + address2 + "," + address3 + "," + addressCity + "," + nationality + ","
					+ invitationOrPreapproved + "," + fatherName + ")";

			logger.info("in createVisaApplicationQuery() for ref id - " + appId + " to prepare query - " + createQuery);

		} catch (JSONException e) {
			logger.error("failed to prepare createVisaApplicationQuery due to - " + e);
			// e.printStackTrace();
		} catch (ParseException e) {
			logger.error("failed to prepare createVisaApplicationQuery due to - " + e);
			// e.printStackTrace();
		}
		return createQuery;
	}

}
