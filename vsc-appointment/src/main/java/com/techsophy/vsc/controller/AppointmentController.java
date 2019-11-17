package com.techsophy.vsc.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.techsophy.vsc.auth.UserAuthentication;
import com.techsophy.vsc.dao.AppointmentDao;
import com.techsophy.vsc.service.AppointmentService;
import com.techsophy.vsc.service.MetricService;

import io.micrometer.core.annotation.Timed;

@RestController
@RequestMapping(value = "/")
@Timed
public class AppointmentController {
	private final Logger logger = LoggerFactory.getLogger(AppointmentController.class);

	@Autowired
	AppointmentService appointmentService;

	@Autowired
	AppointmentDao appointmentDao;

	@Autowired
	MetricService metricService;

	@Autowired
	UserAuthentication authProvider;

	@ExceptionHandler
	void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value());
	}

	@Timed(value = "getAllCountries", longTask = true)
	@RequestMapping(value = "/countries", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getAllCountries() {

		logger.info("In getAllCountries()");

		return appointmentService.getAllCountries();

	}

	@RequestMapping(value = "/visatypes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getVisaTypes(@RequestParam("countryId") String countryId) {
		logger.info("In getVisaTypes()");
		// Validation
		if (countryId == null || countryId.isEmpty()) {
			logger.info("Missing required parameters.");
			throw new IllegalArgumentException("Missing required parameters.");
		}
		return appointmentService.getVisaTypes(countryId);
	}

	@RequestMapping(value = "/gender", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getGender() {
		logger.info("In getGender()");
		return appointmentService.getGender();
	}

	@RequestMapping(value = "/maritalstatus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getMaritalStatus() {
		logger.info("In getMaritalStatus()");
		return appointmentService.getMaritalStatus();
	}

	@RequestMapping(value = "/religion", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getReligion() {
		logger.info("In getAllCountries()");
		return appointmentService.getReligion();
	}

	@RequestMapping(value = "/modeofentry", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getModeOfEntry() {
		logger.info("In getModeOfEntry()");
		return appointmentService.getModeOfEntry();
	}

	@RequestMapping(value = "/portofentry", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getPortOfEntries() {
		logger.info("In getAllCountries()");
		return appointmentService.getPortOfEntries();
	}

	@RequestMapping(value = "/typeofentry", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getTypeOfEntries() {
		logger.info("In getTypeOfEntries()");
		return appointmentService.getTypeOfEntries();
	}

	@RequestMapping(value = "/passporttypes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getPassportTypes(@RequestParam("countryId") String countryId) {
		logger.info("In getPassportTypes() for country - " + countryId);
		// Validation
		if (countryId == null || countryId.isEmpty()) {
			logger.info("Missing required parameters.");
			throw new IllegalArgumentException("Missing required parameters.");
		}
		return appointmentService.getPassportTypes(countryId);
	}

	@RequestMapping(value = "/cities", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getCities(@RequestParam("countryId") String countryId, @RequestParam("scId") String scId) {
		logger.info("In getCities() for country - " + countryId + " and scId: " + scId);
		return appointmentService.getCities(countryId, scId);
	}

	@RequestMapping(value = "/visafee", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getVisaFeeDetails(@RequestParam("visaType") String visaType,
			@RequestParam("countryId") String countryId,
			@RequestParam(value = "noOfVisits", required = false) String noOfVisits) {
		logger.info("In getVisaFeeDetails() for Visa Type - " + visaType + " and for no of visits - " + noOfVisits);
		// Validation
		if (visaType == null || visaType.isEmpty() || countryId == null || countryId.isEmpty()) {
			logger.info("Missing required parameters.");
			throw new IllegalArgumentException("Missing required parameters.");
		}
		return appointmentService.getVisaFeeDetails(visaType, noOfVisits, countryId);
	}

	@RequestMapping(value = "/timeslots", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getAvailableTimeSlots(@RequestParam("serviceId") String serviceId,
			@RequestParam(value = "startDay", required = false) String startDay,
			@RequestParam(value = "endDay", required = false) String endDay) {
		logger.info("In getAvailableTimeSlots() for service center - " + serviceId);
		// Validation
		if (serviceId == null || serviceId.isEmpty() || startDay == null || startDay.isEmpty() || endDay == null
				|| endDay.isEmpty()) {
			logger.info("Missing required parameters.");
			throw new IllegalArgumentException("Missing required parameters.");
		}
		return appointmentService.getAvailableTimeSlots(serviceId, startDay, endDay);
	}

	@Timed(value = "vsc.appointment.requests")
	@RequestMapping(value = "/create", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public String createAppointment(@RequestBody String input) {
		logger.info("In createAppointment()");
		return appointmentService.createAppointment(input);
	}

	@RequestMapping(value = "/appointmentdetails/{appRefId}/{serviceId}", method = RequestMethod.GET)
	public String getAppointmentDetails(@PathVariable("appRefId") String appRefId,
			@PathVariable("serviceId") String serviceId) {
		logger.info("in getAppointmentDetails() for ref if - " + appRefId + " and service center id - " + serviceId);
		return appointmentService.getAppointmentDetails(appRefId, serviceId);
	}

	@RequestMapping(value = "/rescheduleAppDetails/{appRefId}/{serviceId}", method = RequestMethod.GET)
	public String getRescheduleAppDetails(@PathVariable("appRefId") String appRefId,
			@PathVariable("serviceId") String serviceId) {
		logger.info("in getRescheduleAppDetails() for ref if - " + appRefId + " and service center id - " + serviceId);
		return appointmentService.getRescheduleAppDetails(appRefId, serviceId);
	}

	@RequestMapping(value = "/track/byappref", method = RequestMethod.GET)
	public String getTrackDetailsByAppRef(@RequestParam String appRefId) {
		logger.info("in getTrackDetailsByAppRef() to get track details of appl. id - " + appRefId);
		// Validation
		if (appRefId == null || appRefId.isEmpty()) {
			logger.info("Missing required parameters.");
			throw new IllegalArgumentException("Missing required parameters.");
		}
		return appointmentService.getTrackDetailsByAppRef(appRefId);
	}

	@RequestMapping(value = "/fullmetrics", method = RequestMethod.GET)
	@ResponseBody
	public Map getMetric() {
		return metricService.getFullMetric();
	}
}
