package au.unimelb.fht.model;

import java.util.Date;

public class SearchParameter {
	
	private String clinicID;

	private String name;

	private String email;

	private String phone;

	private String vitalSignAlertCode;

	private String mentalHealthAlertCode;

	private String dateCondition;

	private Date dateTime;
	
	public SearchParameter() {
		
	}

	public SearchParameter(String clinicID,String name, String email, String phone, String vitalSignAlertCode,
			String mentalHealthAlertCode, String dateCondition, Date dateTime) {
		super();
		this.clinicID=clinicID;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.vitalSignAlertCode = vitalSignAlertCode;
		this.mentalHealthAlertCode = mentalHealthAlertCode;
		this.dateCondition = dateCondition;
		this.dateTime = dateTime;
	}

	public String getClinicID() {
		return clinicID;
	}

	public void setClinicID(String clinicID) {
		this.clinicID = clinicID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getVitalSignAlertCode() {
		return vitalSignAlertCode;
	}

	public void setVitalSignAlertCode(String vitalSignAlertCode) {
		this.vitalSignAlertCode = vitalSignAlertCode;
	}

	public String getMentalHealthAlertCode() {
		return mentalHealthAlertCode;
	}

	public void setMentalHealthAlertCode(String mentalHealthAlertCode) {
		this.mentalHealthAlertCode = mentalHealthAlertCode;
	}

	public String getDateCondition() {
		return dateCondition;
	}

	public void setDateCondition(String dateCondition) {
		this.dateCondition = dateCondition;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	@Override
	public String toString() {
		return "SearchParameter [clinicID=" + clinicID + ", name=" + name + ", email=" + email + ", phone=" + phone
				+ ", vitalSignAlertCode=" + vitalSignAlertCode + ", mentalHealthAlertCode=" + mentalHealthAlertCode
				+ ", dateCondition=" + dateCondition + ", dateTime=" + dateTime + "]";
	}

}
