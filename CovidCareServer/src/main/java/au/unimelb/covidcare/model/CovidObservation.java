package au.unimelb.covidcare.model;

import java.util.Date;

public class CovidObservation {
		
	private String name;
	
	private String value;
	
	private Date effectiveDateTime;
	
	private CodingDetail codingDetail;
	
	private ValueQuantity valueQuantity;
	
	private ValueCodeableConcept valueCodeableConcept;
	

	public String getName() {
		return name;
	}

	public CovidObservation setName(String name) {
		this.name = name;
		return this;
	}

	public String getValue() {
		return value;
	}

	public CovidObservation setValue(String value) {
		this.value = value;
		return this;
	}

	
	public Date getEffectiveDateTime() {
		return effectiveDateTime;
	}

	public CovidObservation setEffectiveDateTime(Date effectiveDateTime) {
		this.effectiveDateTime = effectiveDateTime;
		return this;
	}

	public CodingDetail getCodingDetail() {
		return codingDetail;
	}

	public CovidObservation setCodingDetail(CodingDetail codingDetail) {
		this.codingDetail = codingDetail;
		return this;
	}

	public ValueQuantity getValueQuantity() {
		return valueQuantity;
	}

	public CovidObservation setValueQuantity(ValueQuantity valueQuantity) {
		this.valueQuantity = valueQuantity;
		return this;
	}

	public ValueCodeableConcept getValueCodeableConcept() {
		return valueCodeableConcept;
	}

	public CovidObservation setValueCodeableConcept(ValueCodeableConcept valueCodeableConcept) {
		this.valueCodeableConcept = valueCodeableConcept;
		return this;
	}

	@Override
	public String toString() {
		return "CovidObservation [name=" + name + ", value=" + value + ", effectiveDateTime=" + effectiveDateTime + "]";
	}

	

	
	

}
