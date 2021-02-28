package au.unimelb.covidcare.util;

/**
 * COVIDCare Alerts
 * Refer "COVIDCare-FHT-Interface-Documentation" for more details.
 *
 * @author smaddumarach
 *
 */
public enum EnumAlertCode {
	
	immediateEmergency("Vital signs emergency alert - Red Status","Vital signs emergency alert: emergency attention recommended"),
	
	immediateAbnormality("Vital signs abnormal alert - Amber Status","Abnormal vital signs alert: re-check recommended"),
	
	trendNegative("Vital signs negative trend - Amber status","Vital signs trend negative alert: re-check recommended"),
	
	trendEmergency("Vital signs negative trend - Red status","Vital signs negative trend alert: emergency attention recommended"),
	
	safe("Vital signs within limits","Vital signs within limits"),
	
	mentalNegative("Abnormal PHQ2 or GAD2 (emotional wellbeing warning)","Emotional wellbeing warning: medical attention recommended"),
	
	mentalSafe("PHQ2 or GAD2 (emotional wellbeing) within limits","PHQ2 or GAD2 (emotional wellbeing) within limits");
	
	private String alert;
	
	private String recomendation;
	 
	EnumAlertCode(String alert,String recomendation) {
        this.alert = alert;
        this.recomendation=recomendation;
    }

	public String getAlert() {
		return alert;
	}

	public void setAlert(String alert) {
		this.alert = alert;
	}

	public String getRecomendation() {
		return recomendation;
	}

	public void setRecomendation(String recomendation) {
		this.recomendation = recomendation;
	}
 
}
