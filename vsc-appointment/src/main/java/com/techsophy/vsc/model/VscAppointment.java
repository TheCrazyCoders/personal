package com.techsophy.vsc.model;

import java.sql.Timestamp;
import java.util.Date;

public class VscAppointment {

	private String appId;

	private String scId;

	private String oldAppId;

	private String appCreatedDate;

	private Date appDate;

	private String slotNo;

	private String appType;

	private String applyingFor;

	private Integer noApplicants;

	private String visaType;

	private String paymentType;

	private Double totalVisaFee;

	private String appStatus;

	private String receptionUserId;

	private Timestamp receptionTime;

	private String paymentUserId;

	private Timestamp paymentTime;

	private String paymentReference;

	private String applicantEmail;

	private Integer isLoungEnabled;

	private Integer isSmsEnabled;

	private Integer isCourierEnabled;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getScId() {
		return scId;
	}

	public void setScId(String scId) {
		this.scId = scId;
	}

	public String getOldAppId() {
		return oldAppId;
	}

	public void setOldAppId(String oldAppId) {
		this.oldAppId = oldAppId;
	}

	public String getAppCreatedDate() {
		return appCreatedDate;
	}

	public void setAppCreatedDate(String appCreatedDate) {
		this.appCreatedDate = appCreatedDate;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public String getSlotNo() {
		return slotNo;
	}

	public void setSlotNo(String slotNo) {
		this.slotNo = slotNo;
	}

	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public String getApplyingFor() {
		return applyingFor;
	}

	public void setApplyingFor(String applyingFor) {
		this.applyingFor = applyingFor;
	}

	public Integer getNoApplicants() {
		return noApplicants;
	}

	public void setNoApplicants(Integer noApplicants) {
		this.noApplicants = noApplicants;
	}

	public String getVisaType() {
		return visaType;
	}

	public void setVisaType(String visaType) {
		this.visaType = visaType;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public Double getTotalVisaFee() {
		return totalVisaFee;
	}

	public void setTotalVisaFee(Double totalVisaFee) {
		this.totalVisaFee = totalVisaFee;
	}

	public String getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(String appStatus) {
		this.appStatus = appStatus;
	}

	public String getReceptionUserId() {
		return receptionUserId;
	}

	public void setReceptionUserId(String receptionUserId) {
		this.receptionUserId = receptionUserId;
	}

	public Timestamp getReceptionTime() {
		return receptionTime;
	}

	public void setReceptionTime(Timestamp receptionTime) {
		this.receptionTime = receptionTime;
	}

	public String getPaymentUserId() {
		return paymentUserId;
	}

	public void setPaymentUserId(String paymentUserId) {
		this.paymentUserId = paymentUserId;
	}

	public Timestamp getPaymentTime() {
		return paymentTime;
	}

	public void setPaymentTime(Timestamp paymentTime) {
		this.paymentTime = paymentTime;
	}

	public String getPaymentReference() {
		return paymentReference;
	}

	public void setPaymentReference(String paymentReference) {
		this.paymentReference = paymentReference;
	}

	public String getApplicantEmail() {
		return applicantEmail;
	}

	public void setApplicantEmail(String applicantEmail) {
		this.applicantEmail = applicantEmail;
	}

	public Integer getIsLoungEnabled() {
		return isLoungEnabled;
	}

	public void setIsLoungEnabled(Integer isLoungEnabled) {
		this.isLoungEnabled = isLoungEnabled;
	}

	public Integer getIsSmsEnabled() {
		return isSmsEnabled;
	}

	public void setIsSmsEnabled(Integer isSmsEnabled) {
		this.isSmsEnabled = isSmsEnabled;
	}

	public Integer getIsCourierEnabled() {
		return isCourierEnabled;
	}

	public void setIsCourierEnabled(Integer isCourierEnabled) {
		this.isCourierEnabled = isCourierEnabled;
	}

}
