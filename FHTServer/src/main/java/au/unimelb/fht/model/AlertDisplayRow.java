package au.unimelb.fht.model;

import java.util.Date;


public class AlertDisplayRow  implements Comparable<AlertDisplayRow>{
	
	private Date checkinDateTime;
	
	private String covidcareID;
	
	private String name;
	
	private String email;
	
	private String phone;
	
	private String postcode;
	
	private String vitalsignAlert;
	
	private String mentalHealthAlert;

	public Date getCheckinDateTime() {
		return checkinDateTime;
	}

	public void setCheckinDateTime(Date checkinDateTime) {
		this.checkinDateTime = checkinDateTime;
	}

	public String getCovidcareID() {
		return covidcareID;
	}

	public void setCovidcareID(String covidcareID) {
		this.covidcareID = covidcareID;
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

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getVitalsignAlert() {
		return vitalsignAlert;
	}

	public void setVitalsignAlert(String vitalsignAlert) {
		this.vitalsignAlert = vitalsignAlert;
	}

	public String getMentalHealthAlert() {
		return mentalHealthAlert;
	}

	public void setMentalHealthAlert(String mentalHealthAlert) {
		this.mentalHealthAlert = mentalHealthAlert;
	}
	
	@Override
	public int compareTo(AlertDisplayRow o) {
		return o.getCheckinDateTime().compareTo(this.getCheckinDateTime());
	}

	@Override
	public String toString() {
		return "CombinedAlert [checkinDateTime=" + checkinDateTime + ", covidcareID=" + covidcareID + ", name=" + name
				+ ", email=" + email + ", phone=" + phone + ", postcode=" + postcode + ", vitalsignAlert="
				+ vitalsignAlert + ", mentalHealthAlert=" + mentalHealthAlert + "]";
	}
	
	

}
