package au.unimelb.covidcare.model;

import java.math.BigDecimal;

/**
 * 
 * COVIDCare Check-in PROM "valueDetails" are transferred by this object. 
 * Refer "COVIDCare-FHT-Interface-Documentation" for more details.
 * 
 * @author smaddumarach
 *
 */
public class ValueQuantity {
	
	private BigDecimal value;
	
	private String unit;
	
	private String system;
	
	private String code;

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

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
	

}
