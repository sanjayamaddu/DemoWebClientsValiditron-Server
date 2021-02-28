package au.unimelb.covidcare.model;

import java.util.Date;
import java.util.List;

public class CovidEncounter   {
	
	private String checkInID;
	
	private Date checkinDateTime;
	
	private String reasonOrPromptedBy;
	
	private String covidcareID;
	
    private List<CovidObservation>	covidObservationlst;
    
    private List<CovidDetectedIssue> covidDetectedIssuelst;
    

	public String getCheckInID() {
		return checkInID;
	}

	public CovidEncounter setCheckInID(String checkInID) {
		this.checkInID = checkInID;
		return this;
	}

	public Date getCheckinDateTime() {
		return checkinDateTime;
	}

	public CovidEncounter setCheckinDateTime(Date checkinDateTime) {
		this.checkinDateTime = checkinDateTime;
		return this;
	}

	public String getReasonOrPromptedBy() {
		return reasonOrPromptedBy;
	}

	public CovidEncounter setReasonOrPromptedBy(String reasonOrPromptedBy) {
		this.reasonOrPromptedBy = reasonOrPromptedBy;
		return this;
	}

	public String getCovidcareID() {
		return covidcareID;
	}

	public CovidEncounter setCovidcareID(String covidcareID) {
		this.covidcareID = covidcareID;
		return this;
	}
	
	public List<CovidObservation> getCovidObservationlst() {
		return covidObservationlst;
	}

	public CovidEncounter setCovidObservationlst(List<CovidObservation> covidObservationlst) {
		this.covidObservationlst = covidObservationlst;
		return this;
	}

	public List<CovidDetectedIssue> getCovidDetectedIssuelst() {
		return covidDetectedIssuelst;
	}

	public CovidEncounter setCovidDetectedIssuelst(List<CovidDetectedIssue> covidDetectedIssuelst) {
		this.covidDetectedIssuelst = covidDetectedIssuelst;
		return this;
	}

	@Override
	public String toString() {
		return "CovidEncounter [checkInID=" + checkInID + ", checkinDateTime=" + checkinDateTime
				+ ", reasonOrPromptedBy=" + reasonOrPromptedBy + ", covidcareID=" + covidcareID
				+ ", covidObservationlst=" + covidObservationlst + ", covidDetectedIssuelst=" + covidDetectedIssuelst
				+ "]";
	}

	

}
