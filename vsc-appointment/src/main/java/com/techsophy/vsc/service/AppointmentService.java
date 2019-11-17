package com.techsophy.vsc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techsophy.vsc.dao.AppointmentDao;

@Service
public class AppointmentService {

	@Autowired
	AppointmentDao appointmentDao;

	public String getAllCountries() {
		return appointmentDao.getAllCountries();
	}

	public String getVisaTypes(String countryId) {
		return appointmentDao.getVisaTypes(countryId);
	}

	public String getGender() {
		return appointmentDao.getGender();
	}

	public String getMaritalStatus() {
		return appointmentDao.getMaritalStatus();
	}

	public String getReligion() {
		return appointmentDao.getReligion();
	}

	public String getModeOfEntry() {
		return appointmentDao.getModeOfEntry();
	}

	public String getPortOfEntries() {
		return appointmentDao.getPortOfEntries();
	}

	public String getTypeOfEntries() {
		return appointmentDao.getTypeOfEntries();
	}

	public String getPassportTypes(String countryId) {
		return appointmentDao.getPassportTypes(countryId);
	}

	public String getCities(String countryId, String scId) {
		return appointmentDao.getCities(countryId, scId);
	}

	public String getVisaFeeDetails(String visaType, String noOfVisits, String countryId) {
		return appointmentDao.getVisaFeeDetails(visaType, noOfVisits, countryId);
	}

	public String getAvailableTimeSlots(String serviceId, String startDay, String endDay) {
		return appointmentDao.getAvailableTimeSlots(serviceId, startDay, endDay);
	}

	public String createAppointment(String input) {

		return appointmentDao.createAppointment(input);
	}

	public String getAppointmentDetails(String appRefId, String serviceId) {
		return appointmentDao.getAppointmentDetails(appRefId, serviceId);
	}

	public String getTrackDetailsByAppRef(String appRefId) {
		return appointmentDao.getTrackDetailsByAppRef(appRefId);
	}

	public String getRescheduleAppDetails(String appRefId, String serviceId) {
		return appointmentDao.getRescheduleAppDetails(appRefId, serviceId);
	}

}
