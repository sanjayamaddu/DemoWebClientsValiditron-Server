package au.unimelb.covidcare.model;

public class CovidOrganization {
	
	private String referralClinicID;
	
	private String referralClinicName;

	public String getReferralClinicID() {
		return referralClinicID;
	}

	public CovidOrganization setReferralClinicID(String referralClinicID) {
		this.referralClinicID = referralClinicID;
		return this;
	}

	public String getReferralClinicName() {
		return referralClinicName;
	}

	public CovidOrganization setReferralClinicName(String referralClinicName) {
		this.referralClinicName = referralClinicName;
		return this;
	}

	@Override
	public String toString() {
		return "CovidOrganization [referralClinicID=" + referralClinicID + ", referralClinicName=" + referralClinicName
				+ "]";
	}

}
