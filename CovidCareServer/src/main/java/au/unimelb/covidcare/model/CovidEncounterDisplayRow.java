package au.unimelb.covidcare.model;

import java.util.Date;
import java.util.List;

public class CovidEncounterDisplayRow implements Comparable<CovidEncounterDisplayRow> {
	
	private String checkInID;
	
	private Date checkinDateTime;
	
	private String reasonOrPromptedBy;
	
	private String covidcareID;
	
    private String patientName;
    
    private String vitalSignAlert;
    
    private String mentalHealthAlert;

	public String getCheckInID() {
		return checkInID;
	}

	public CovidEncounterDisplayRow setCheckInID(String checkInID) {
		this.checkInID = checkInID;
		return this;
	}

	public Date getCheckinDateTime() {
		return checkinDateTime;
	}

	public CovidEncounterDisplayRow setCheckinDateTime(Date checkinDateTime) {
		this.checkinDateTime = checkinDateTime;
		return this;
	}

	public String getReasonOrPromptedBy() {
		return reasonOrPromptedBy;
	}

	public CovidEncounterDisplayRow setReasonOrPromptedBy(String reasonOrPromptedBy) {
		this.reasonOrPromptedBy = reasonOrPromptedBy;
		return this;
	}

	public String getCovidcareID() {
		return covidcareID;
	}

	public CovidEncounterDisplayRow setCovidcareID(String covidcareID) {
		this.covidcareID = covidcareID;
		return this;
	}

	public String getPatientName() {
		return patientName;
	}

	public CovidEncounterDisplayRow setPatientName(String patientName) {
		this.patientName = patientName;
		return this;
	}

	public String getVitalSignAlert() {
		return vitalSignAlert;
	}

	public CovidEncounterDisplayRow setVitalSignAlert(String vitalSignAlert) {
		this.vitalSignAlert = vitalSignAlert;
		return this;
	}

	public String getMentalHealthAlert() {
		return mentalHealthAlert;
	}

	public CovidEncounterDisplayRow setMentalHealthAlert(String mentalHealthAlert) {
		this.mentalHealthAlert = mentalHealthAlert;
		return this;
	}
	@Override
	public int compareTo(CovidEncounterDisplayRow o) {
		return o.getCheckinDateTime().compareTo(this.getCheckinDateTime());
	}

	@Override
	public String toString() {
		return "CovidEncounterDisplayRow [checkInID=" + checkInID + ", checkinDateTime=" + checkinDateTime
				+ ", reasonOrPromptedBy=" + reasonOrPromptedBy + ", covidcareID=" + covidcareID + ", patientName="
				+ patientName + ", vitalSignAlert=" + vitalSignAlert + ", mentalHealthAlert=" + mentalHealthAlert + "]";
	}

}
