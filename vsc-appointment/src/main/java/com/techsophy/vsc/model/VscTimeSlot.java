package com.techsophy.vsc.model;

import java.util.Date;

public class VscTimeSlot {

	private String scId;

	private String slotNo;

	private Date day;

	private Integer scheduledNormalVisits;

	private Integer scheduledPriorityVisits;

	public String getScId() {
		return scId;
	}

	public void setScId(String scId) {
		this.scId = scId;
	}

	public String getSlotNo() {
		return slotNo;
	}

	public void setSlotNo(String slotNo) {
		this.slotNo = slotNo;
	}

	public Date getDay() {
		return day;
	}

	public void setDay(Date day) {
		this.day = day;
	}

	public Integer getScheduledNormalVisits() {
		return scheduledNormalVisits;
	}

	public void setScheduledNormalVisits(Integer scheduledNormalVisits) {
		this.scheduledNormalVisits = scheduledNormalVisits;
	}

	public Integer getScheduledPriorityVisits() {
		return scheduledPriorityVisits;
	}

	public void setScheduledPriorityVisits(Integer scheduledPriorityVisits) {
		this.scheduledPriorityVisits = scheduledPriorityVisits;
	}

}
