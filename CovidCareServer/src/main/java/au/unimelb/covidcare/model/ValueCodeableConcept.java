package au.unimelb.covidcare.model;

/**
 * 
 * [COVIDCare field/COVIDCare Check-in PROM] "valueDetails" are transferred by this object. 
 * Refer "COVIDCare-FHT-Interface-Documentation" for more details.
 * 
 * @author smaddumarach
 *
 */
public class ValueCodeableConcept {
	
	private String system;
	
	private String code;
	
	private String text;

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	
	
	

}
