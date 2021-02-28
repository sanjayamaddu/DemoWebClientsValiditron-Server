package au.unimelb.covidcare.model;

import java.util.Date;

public class CovidDetectedIssue {
	
	private String name;
	
	private String status;
	
	private String severity;
	
	private Date effectiveDateTime;
	
	private CodingDetail codingDetail;
	
	private String text;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public Date getEffectiveDateTime() {
		return effectiveDateTime;
	}

	public void setEffectiveDateTime(Date effectiveDateTime) {
		this.effectiveDateTime = effectiveDateTime;
	}

	public CodingDetail getCodingDetail() {
		return codingDetail;
	}

	public void setCodingDetail(CodingDetail codingDetail) {
		this.codingDetail = codingDetail;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "CovidDetectedIssue [name=" + name + ", status=" + status + ", severity=" + severity
				+ ", effectiveDateTime=" + effectiveDateTime + ", text=" + text + "]";
	}
	
	

}
