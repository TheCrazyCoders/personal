package com.techsophy.vsc.model;

public class VscServiceCenter {


	private String scId;

	private String name;

	private String address;

	private String city;

	private String countryId;

	private String parentId;

	public String getScId() {
		return scId;
	}

	public void setScId(String scId) {
		this.scId = scId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountryId() {
		return countryId;
	}

	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

//	public VscAppointment getVscAppointment() {
//		return vscAppointment;
//	}
//
//	public void setVscAppointment(VscAppointment vscAppointment) {
//		this.vscAppointment = vscAppointment;
//	}
}
