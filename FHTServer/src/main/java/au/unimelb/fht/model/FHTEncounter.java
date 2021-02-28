package au.unimelb.fht.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "encounter")
public class FHTEncounter implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@Column(name = "checkInID")
	private String checkInID;
	
	@Column(name = "covidcareID")
	private String covidcareID;
	
	@Column(name = "fhirID")
	private String fhirID;
	
	@Column(name = "promptedBy")
	private String reasonOrPromptedBy;
	
	@Column(name = "checkinDateTime")
	private Date checkinDateTime;
	
	@OneToOne
    @JoinColumn(name="patient", nullable=false)
	private FHTPatient fhtpatient;
	
	
	@JsonIgnore
	@OneToMany(mappedBy="fhtEncounter", cascade = CascadeType.ALL)
    private List<FHTAlert> fhtAlerts;

	public String getCheckInID() {
		return checkInID;
	}

	public FHTEncounter setCheckInID(String checkInID) {
		this.checkInID = checkInID;
		return this;
	}

	public String getCovidcareID() {
		return covidcareID;
	}

	public FHTEncounter setCovidcareID(String covidcareID) {
		this.covidcareID = covidcareID;
		return this;
	}
	public String getFhirID() {
		return fhirID;
	}

	public FHTEncounter setFhirID(String fhirID) {
		this.fhirID = fhirID;
		return this;
	}

	public FHTPatient getFhtpatient() {
		return fhtpatient;
	}

	public FHTEncounter setFhtpatient(FHTPatient fhtpatient) {
		this.fhtpatient = fhtpatient;
		return this;
	}
	public String getReasonOrPromptedBy() {
		return reasonOrPromptedBy;
	}

	public FHTEncounter setReasonOrPromptedBy(String reasonOrPromptedBy) {
		this.reasonOrPromptedBy = reasonOrPromptedBy;
		return this;
	}

	public Date getCheckinDateTime() {
		return checkinDateTime;
	}

	public FHTEncounter setCheckinDateTime(Date checkinDateTime) {
		this.checkinDateTime = checkinDateTime;
		return this;
	}

	public List<FHTAlert> getFhtAlerts() {
		return fhtAlerts;
	}

	public FHTEncounter setFhtAlerts(List<FHTAlert> fhtAlerts) {
		this.fhtAlerts = fhtAlerts;
		return this;
	}

	@Override
	public String toString() {
		return "FHTEncounter [id=" + id + ", checkInID=" + checkInID + ", covidcareID=" + covidcareID + ", fhirID="
				+ fhirID + ", reasonOrPromptedBy=" + reasonOrPromptedBy + ", checkinDateTime=" + checkinDateTime
				+ ", fhtpatient=" + fhtpatient + ", fhtAlerts=" + fhtAlerts + "]\n";
	}
	
	

}
