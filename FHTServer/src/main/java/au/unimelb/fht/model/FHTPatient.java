package au.unimelb.fht.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "patient")
public class FHTPatient implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@Column(name = "clinicID")
	private String clinicID;
	
	@Column(name = "fhirID")
	private String fhirID;
	
	@Column(name = "covidcareID")
	private String covidcareID;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "phone")
	private String phone;
	
	@Column(name = "postcode")
	private String postcode;
	
	@JsonIgnore
	@OneToMany(mappedBy="fhtpatient", cascade = CascadeType.ALL)
    private List<FHTEncounter> fhtEncounters;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getClinicID() {
		return clinicID;
	}

	public String getFhirID() {
		return fhirID;
	}

	public void setFhirID(String fhirID) {
		this.fhirID = fhirID;
	}

	public void setClinicID(String clinicID) {
		this.clinicID = clinicID;
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
	public List<FHTEncounter> getFhtEncounters() {
		return fhtEncounters;
	}

	public void setFhtEncounters(List<FHTEncounter> fhtEncounters) {
		this.fhtEncounters = fhtEncounters;
	}

	@Override
	public String toString() {
		return "FHTPatient [id=" + id + ", clinicID=" + clinicID + ", fhirID=" + fhirID + ", covidcareID=" + covidcareID
				+ ", name=" + name + ", email=" + email + ", phone=" + phone + ", postcode=" + postcode + "]";
	}
	
	
	
}
