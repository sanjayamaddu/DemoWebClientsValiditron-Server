package au.unimelb.fht.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "alert")
public class FHTAlert implements Serializable {
	
	@Id
	//@GeneratedValue(strategy = GenerationType.AUTO)
	@SequenceGenerator(name = "alert_generator", sequenceName = "SETTING_SEQ")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "alert_generator")
	private int alertid;
	
	@OneToOne
    @JoinColumn(name="alerttype", nullable=false)
	private  AlertType alertType;
	
	@Column(name = "alertcode")
	private String alertCode;
	
	@Column(name = "alert")
	private String alert;
	
	@Column(name = "recommendation")
	private String recommendation;
	
	@Column(name = "severity")
	private String severity;
	
	@Column(name = "effectiveDateTime")
	private Date effectiveDateTime;
	
	@ManyToOne
    @JoinColumn(name="encounter", nullable=false)
	private FHTEncounter fhtEncounter;
	
	public AlertType getAlertType() {
		return alertType;
	}

	public void setAlertType(AlertType alertType) {
		this.alertType = alertType;
	}

	public Date getEffectiveDateTime() {
		return effectiveDateTime;
	}

	public void setEffectiveDateTime(Date effectiveDateTime) {
		this.effectiveDateTime = effectiveDateTime;
	}
	
	
	public String getAlertCode() {
		return alertCode;
	}

	public void setAlertCode(String alertcode) {
		this.alertCode = alertcode;
	}

	public String getAlert() {
		return alert;
	}

	public void setAlert(String alert) {
		this.alert = alert;
	}

	public String getRecommendation() {
		return recommendation;
	}

	public void setRecommendation(String recommendation) {
		this.recommendation = recommendation;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public FHTEncounter getFhtEncounter() {
		return fhtEncounter;
	}

	public void setFhtEncounter(FHTEncounter fhtEncounter) {
		this.fhtEncounter = fhtEncounter;
	}

	@Override
	public String toString() {
		return "FHTAlert [alertid=" + alertid + ", alertType=" + alertType + ", alertCode=" + alertCode + ", alert="
				+ alert + ", recommendation=" + recommendation + ", severity=" + severity + ", effectiveDateTime="
				+ effectiveDateTime + "]";
	}

	
	
	
}
