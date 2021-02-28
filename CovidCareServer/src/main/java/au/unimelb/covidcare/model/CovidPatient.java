package au.unimelb.covidcare.model;

import java.util.Date;
import java.util.List;

public class CovidPatient implements Comparable<CovidPatient>  {
	
	private String covidCareId;
	
	private String name;
	
	private String gender;
	
	private Integer age;
	
	private String email;
	
	private String mobile;
		
	private String postcode;
	
	private Date registerdDate;
	
	private CovidOrganization covidOrganization;
	
	private String referralcode;
	
	private List<CovidObservation>	covidObservationlst;
	
	
	public CovidPatient(CovidOrganization covidOrganization,String covidCareId) {
		this.covidOrganization=covidOrganization;
		this.covidCareId=covidCareId;
	}
	
	public String getCovidCareId() {
		return covidCareId;
	}

	

	public String getName() {
		return name;
	}

	public CovidPatient setName(String name) {
		this.name = name;
		return this;
	}

	public String getGender() {
		return gender;
	}

	public CovidPatient setGender(String gender) {
		this.gender = gender;
		return this;
	}

	public Integer getAge() {
		return age;
	}

	public CovidPatient setAge(Integer age) {
		this.age = age;
		return this;
	}
	
	public String getEmail() {
		return email;
	}

	public CovidPatient setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getMobile() {
		return mobile;
	}

	public CovidPatient setMobile(String mobile) {
		this.mobile = mobile;
		return this;
	}

	public String getPostcode() {
		return postcode;
	}

	public CovidPatient setPostcode(String postcode) {
		this.postcode = postcode;
		return this;
	}

	public Date getRegisterdDate() {
		return registerdDate;
	}

	public CovidPatient setRegisterdDate(Date registerdDate) {
		this.registerdDate = registerdDate;
		return this;
	}

	
	public String getReferralcode() {
		return referralcode;
	}

	public CovidPatient setReferralcode(String referralcode) {
		this.referralcode = referralcode;
		return this;
	}
	
	public List<CovidObservation> getCovidObservationlst() {
		return covidObservationlst;
	}

	public CovidPatient setCovidObservationlst(List<CovidObservation> covidObservationlst) {
		this.covidObservationlst = covidObservationlst;
		return this;
	}
	@Override
	public int compareTo(CovidPatient o) {
		return o.getRegisterdDate().compareTo(this.getRegisterdDate());
	}

	public CovidOrganization getCovidOrganization() {
		return covidOrganization;
	}

	@Override
	public String toString() {
		return "CovidPatient [covidCareId=" + covidCareId + ", name=" + name + ", gender=" + gender + ", age=" + age
				+ ", email=" + email + ", mobile=" + mobile + ", postcode=" + postcode + ", registerdDate="
				+ registerdDate + ", covidOrganization=" + covidOrganization + ", referralcode=" + referralcode
				+ ", covidObservationlst=" + covidObservationlst + "]";
	}
	
	
	

	

	
	

}
