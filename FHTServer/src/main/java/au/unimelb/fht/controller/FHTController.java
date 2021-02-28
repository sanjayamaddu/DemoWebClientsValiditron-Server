package au.unimelb.fht.controller;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.time.DateUtils;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DetectedIssue;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import au.unimelb.fht.model.AlertDisplayRow;
import au.unimelb.fht.model.AlertType;
import au.unimelb.fht.model.CovidOrganization;
import au.unimelb.fht.model.FHTAlert;
import au.unimelb.fht.model.FHTEncounter;
import au.unimelb.fht.model.FHTPatient;
import au.unimelb.fht.model.LastUpdateDetails;
import au.unimelb.fht.model.SearchParameter;
import au.unimelb.fht.repo.AlertRepository;
import au.unimelb.fht.repo.AlertTypeRepository;
import au.unimelb.fht.repo.EncounterRepository;
import au.unimelb.fht.repo.FHTPatientCustomRepository;
import au.unimelb.fht.repo.PatientRepository;
import au.unimelb.fht.util.FHTConstants;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;

/**
 * 
 * @author smaddumarach
 *
 */
//@CrossOrigin(origins = { "${cross.origin.path}" })
@RestController
@RequestMapping("/api")
public class FHTController {

	private final static Logger LOGGER = Logger.getLogger(FHTController.class.getName());

	@Value("${server.base}")
	private String serverBase;
	@Value("${smilecdr.username}")
	private String username;
	@Value("${smilecdr.password}")
	private String password;
	@Value("${systemcode.patient}")
	private String SYSTEM_CODE_COVIDCARE_AU_APP_PATIENT;
	@Value("${systemcode.encounter}")
	private String SYSTEM_CODE_COVIDCARE_AU_APP_ENCOUNTER;
	@Autowired
	PatientRepository patientRepository;
	@Autowired
	AlertTypeRepository alertTypeRepository;
	@Autowired
	AlertRepository alertRepository;
	@Autowired
	FHTPatientCustomRepository FHTPatientCustomRepository; 
	@Autowired
	EncounterRepository encounterRepository;
	
	
	private IGenericClient client;
	
	@PostConstruct
	private void createFhirContext() {
		FhirContext ctx = FhirContext.forR4();
		IClientInterceptor authInterceptor = new BasicAuthInterceptor(username,password);
		IGenericClient client = ctx.newRestfulGenericClient(serverBase);
		client.registerInterceptor(authInterceptor);
		this.client=client;
		
	}
	
	
	@RequestMapping("/fetchnewalerts/{practitionerID}")
	public LastUpdateDetails fetchnewalerts(@PathVariable("practitionerID")String practitionerID) {
		setupalerttypes();
		addnewpatients(practitionerID);
		List<FHTPatient> fhtPatients= (List<FHTPatient>) patientRepository.findByclinicID(practitionerID);
		for (FHTPatient fhtPatient : fhtPatients) {
			setupEncountersForPatient(fhtPatient);
		}
		return new LastUpdateDetails().setUpdated(new Date());
	}

	/**
	 * Get alerts by alertType
	 * 
	 * @return
	 */
	@RequestMapping("/getAlertsByTypeName/{typeName}")
	public List<FHTAlert> getFHTAlertsByAlertType(@PathVariable("typeName") String typeName) {
		Iterable<FHTAlert> fhtAlerts = alertRepository.findAllByAlertType(alertTypeRepository.findByName(typeName));
		System.out.println(fhtAlerts);
		return (List<FHTAlert>) fhtAlerts;

	}
	
	/**
	 * Get alerts by alertType
	 * 
	 * @return
	 */
	@RequestMapping("/getDistinctAlertsByTypeName/{typeName}")
	public List<String> getDistinctFHTAlertsByAlertType(@PathVariable("typeName") String typeName) {
		List<String> fhtAlerts = alertRepository.findDistinctAlertCodeByAlertType(alertTypeRepository.findByName(typeName));
		System.out.println(fhtAlerts);
		return fhtAlerts;

	}
	@RequestMapping(value="/getreferrals",method = RequestMethod.GET)
	public List<CovidOrganization> getAllReferrals() {
		Bundle bundle = client.search().forResource(Organization.class)
				.returnBundle(Bundle.class).execute();
		List<CovidOrganization> covidOrganizations = new ArrayList<CovidOrganization>();
		bundle.getEntry().forEach(entry -> {
			if (entry.getResource() instanceof Organization) {
				Organization organization = (Organization) entry.getResource();
				CovidOrganization covidOrganization=new CovidOrganization();
				covidOrganization.setReferralClinicID(organization.getIdElement().getIdPart())
				.setReferralClinicName(organization.getName());
				covidOrganizations.add(covidOrganization);
			}
		});
		LOGGER.info(covidOrganizations.toString());
		return covidOrganizations;
	}
	

	/**
	 * get the list of combined alters.
	 * @param covidPatient
	 * @return
	 */
	@PostMapping("/getalertsbysearchcriteria/")
	public List<AlertDisplayRow> getalertsbysearchcriteria(@RequestBody SearchParameter searchParameter) {
		LOGGER.info(searchParameter.toString());
		List<FHTEncounter> encounters = new ArrayList<FHTEncounter>();
		List<FHTEncounter> clinicrelatedencounters = new ArrayList<FHTEncounter>();
		List<FHTEncounter> ultimateencounters = new ArrayList<FHTEncounter>();
		List<FHTEncounter> alertcodefilterdedpatientrelatedencounters = new ArrayList<FHTEncounter>();
		List<AlertDisplayRow> alertdisplayrows = new ArrayList<AlertDisplayRow>();
		// 1-get patient with criteria
		
		List<FHTPatient> fhtPatients = null;
		if(searchParameter!=null && searchParameter.getClinicID()!=null) {
			if(searchParameter.getName()!=null && searchParameter.getEmail()==null && searchParameter.getPhone()==null ) {
				fhtPatients = FHTPatientCustomRepository.findFHTPatientByClinicIDAndName(searchParameter.getClinicID(), searchParameter.getName());
			}else if(searchParameter.getName()==null && searchParameter.getEmail()!=null && searchParameter.getPhone()==null) {
				fhtPatients = FHTPatientCustomRepository.findFHTPatientByClinicIDAndEmail(searchParameter.getClinicID(), searchParameter.getEmail());
			}else if(searchParameter.getName()==null && searchParameter.getEmail()==null && searchParameter.getPhone()!=null) {
				fhtPatients = FHTPatientCustomRepository.findFHTPatientByClinicIDAndPhone(searchParameter.getClinicID(), searchParameter.getPhone());
			}else if(searchParameter.getName()!=null && searchParameter.getEmail()!=null && searchParameter.getPhone()==null) {
				fhtPatients = FHTPatientCustomRepository.findFHTPatientByClinicIDAndNameAndEmail(searchParameter.getClinicID(), searchParameter.getName(),searchParameter.getEmail());
			}else if(searchParameter.getName()!=null && searchParameter.getEmail()==null && searchParameter.getPhone()!=null) {
				fhtPatients = FHTPatientCustomRepository.findFHTPatientByClinicIDAndNameAndPhone(searchParameter.getClinicID(), searchParameter.getName(),searchParameter.getPhone());
			}else if(searchParameter.getName()==null && searchParameter.getEmail()!=null && searchParameter.getPhone()!=null) {
				fhtPatients = FHTPatientCustomRepository.findFHTPatientByClinicIDAndEmailAndPhone(searchParameter.getClinicID(), searchParameter.getEmail(),searchParameter.getPhone());
			}else if(searchParameter.getName()!=null && searchParameter.getEmail()!=null && searchParameter.getPhone()!=null) {
				fhtPatients = FHTPatientCustomRepository.findFHTPatientByClinicIDAndNameAndEmailAndPhone(searchParameter.getClinicID(), searchParameter.getName(),searchParameter.getEmail(),searchParameter.getPhone());
			}
		}else {
			LOGGER.warning("No Clinical ID is set.");
		}
		// 2-get encounters according to date condition(before/after).
		if (searchParameter.getDateTime()!=null && searchParameter.getDateCondition().equals(FHTConstants.BEFORE)) {
			encounters = encounterRepository.findByCheckinDateTimeBeforeOrderByCheckinDateTimeDesc(searchParameter.getDateTime());
		}else if (searchParameter.getDateTime()!=null && searchParameter.getDateCondition().equals(FHTConstants.AFTER)) {
			encounters = encounterRepository.findByCheckinDateTimeAfterOrderByCheckinDateTimeDesc(searchParameter.getDateTime());
		}else if (searchParameter.getDateTime()!=null && searchParameter.getDateCondition().equals(FHTConstants.ON)) {
			Date startOfDay=DateUtils.truncate(searchParameter.getDateTime(), Calendar.DATE);
			Date endOfDay=DateUtils.addDays(startOfDay, 1);
			encounters = encounterRepository.findByCheckinDateTimeBetweenOrderByCheckinDateTimeDesc(startOfDay,endOfDay);
		}else {
			encounters=(List<FHTEncounter>) encounterRepository.findAll();
		}
		//3-filter encounters to clinic
		for (FHTEncounter fhtEncounter : encounters) {
			if(fhtEncounter.getFhtpatient().getClinicID().equals(searchParameter.getClinicID())) {
				clinicrelatedencounters.add(fhtEncounter);
			}
			
		}
		//4-ignore patient pick if name,email & phone not selected.
		if (searchParameter.getName() == null && searchParameter.getEmail() == null
				&& searchParameter.getPhone() == null) {
			ultimateencounters = clinicrelatedencounters;
		} else {
		// 5-add 1+2 {pick selected patients encounters only}
			for (FHTPatient fhtPatient : fhtPatients) {
				for (FHTEncounter fhtEncounter : clinicrelatedencounters) {
					if (fhtEncounter.getFhtpatient().getFhirID().equals(fhtPatient.getFhirID())) {
						ultimateencounters.add(fhtEncounter);
					}
				}
			}
		}
		//6 filter by combos vital and mental altert
		for (FHTEncounter fhtEncounter : ultimateencounters) {
			FHTAlert fhtAlert_vital  =	returnFHTAlertByTypeFromList(fhtEncounter.getFhtAlerts(),FHTConstants.VITAL_SIGNS);
			FHTAlert fhtAlert_mental =	returnFHTAlertByTypeFromList(fhtEncounter.getFhtAlerts(),FHTConstants.MENTAL_HEALTH);	
			if(searchParameter.getVitalSignAlertCode()!=null && searchParameter.getMentalHealthAlertCode()!=null ) {
				if(fhtAlert_vital.getAlertCode().equals(searchParameter.getVitalSignAlertCode())&&
				   fhtAlert_mental.getAlertCode().equals(searchParameter.getMentalHealthAlertCode())) {
					alertcodefilterdedpatientrelatedencounters.add(fhtEncounter);
				}
			}else if(searchParameter.getVitalSignAlertCode()==null && searchParameter.getMentalHealthAlertCode()!=null ) {
				if(fhtAlert_mental.getAlertCode().equals(searchParameter.getMentalHealthAlertCode())) {
					alertcodefilterdedpatientrelatedencounters.add(fhtEncounter);
				}
			}else if(searchParameter.getVitalSignAlertCode()!=null && searchParameter.getMentalHealthAlertCode()==null ) {
				if(fhtAlert_vital.getAlertCode().equals(searchParameter.getVitalSignAlertCode())) {
					alertcodefilterdedpatientrelatedencounters.add(fhtEncounter);
				}
				
			}else if(searchParameter.getVitalSignAlertCode()==null && searchParameter.getMentalHealthAlertCode()==null) {
				alertcodefilterdedpatientrelatedencounters.add(fhtEncounter);
			}
			
		}
		//create response rows for tabulate
		for (FHTEncounter fhtEncounter : alertcodefilterdedpatientrelatedencounters) {
			AlertDisplayRow alertDisplayRow = new AlertDisplayRow();
			alertDisplayRow.setCheckinDateTime(fhtEncounter.getCheckinDateTime());
			alertDisplayRow.setCovidcareID(fhtEncounter.getFhtpatient().getCovidcareID());
			alertDisplayRow.setName(fhtEncounter.getFhtpatient().getName());
			alertDisplayRow.setEmail(fhtEncounter.getFhtpatient().getEmail());
			alertDisplayRow.setPhone(fhtEncounter.getFhtpatient().getPhone());
			alertDisplayRow.setPostcode(fhtEncounter.getFhtpatient().getPostcode());
			for (FHTAlert alert : fhtEncounter.getFhtAlerts()) {
				if(alert.getAlertType() != null && alert.getAlertType().getName().equals(FHTConstants.VITAL_SIGNS)) {alertDisplayRow.setVitalsignAlert(alert.getAlertCode());}
				if(alert.getAlertType() != null && alert.getAlertType().getName().equals(FHTConstants.MENTAL_HEALTH)){alertDisplayRow.setMentalHealthAlert(alert.getAlertCode());}
			}
			alertdisplayrows.add(alertDisplayRow);
		}
		LOGGER.info("Size: "+alertdisplayrows.size());
		LOGGER.info(alertdisplayrows.toString());
		Collections.sort(alertdisplayrows);
		return alertdisplayrows;
	}
	
	/**
	 * set up the all alert types.
	 */
	private void setupalerttypes() {
		List<AlertType> alertTypes = new ArrayList<AlertType>();
		AlertType alertTypeVitalsigns = new AlertType();
		alertTypeVitalsigns.setName(FHTConstants.VITAL_SIGNS);
		alertTypeVitalsigns.setDescription(FHTConstants.ALERTS_BASED_ON_VITAL_SIGNS);
		AlertType alertTypeMantalHealth = new AlertType();
		alertTypeMantalHealth.setName(FHTConstants.MENTAL_HEALTH);
		alertTypeMantalHealth.setDescription(FHTConstants.ALERTS_BASED_ON_MENTAL_HEALTH_ASSESSMENTS);
		alertTypes.add(alertTypeVitalsigns);
		alertTypes.add(alertTypeMantalHealth);
		for (AlertType alertType : alertTypes) {
			// if already not there save it.
			if (!alertTypeRepository.existsByName(alertType.getName())) {
				alertTypeRepository.save(alertType);
			}
		}

	}

	/**
	 * set up the patients
	 * 
	 * @return
	 */
	private void addnewpatients(String practitionerID) {
		Bundle bundle = client.search().forResource(Patient.class)
				.where(Patient.IDENTIFIER.hasSystemWithAnyCode(SYSTEM_CODE_COVIDCARE_AU_APP_PATIENT))
				.and(Patient.GENERAL_PRACTITIONER.hasId(practitionerID)).returnBundle(Bundle.class).execute();
		List<FHTPatient> newPatientLst = new ArrayList<FHTPatient>();
		bundle.getEntry().forEach(entry -> {
			if (entry.getResource() instanceof Patient) {
				Patient patient = (Patient) entry.getResource();
				if (!patientRepository.existsByFhirID(patient.getIdElement().getIdPart())) {
					FHTPatient fhtPatient = new FHTPatient();
					fhtPatient.setClinicID(practitionerID);
					fhtPatient.setFhirID(patient.getIdElement().getIdPart());
					fhtPatient.setCovidcareID(patient.getIdentifier().get(0).getValue());
					fhtPatient.setName(patient.getName().get(0).getGivenAsSingleString());
					fhtPatient.setEmail(patient.getTelecom().get(1).getValue());
					fhtPatient.setPhone(patient.getTelecom().get(0).getValue());
					fhtPatient.setPostcode(patient.getAddress().get(0).getPostalCode());
					newPatientLst.add(fhtPatient);
				}
			}
		});
		System.out.println(newPatientLst);
		patientRepository.saveAll(newPatientLst);

	}

	/**
	 * Set up encounters only for the selected patients by organization.
	 * 
	 * @param fhtPatient
	 */
	private void setupEncountersForPatient(FHTPatient fhtPatient) {
		Bundle bundle = client.search().forResource(Encounter.class)
				.include(Encounter.INCLUDE_SUBJECT)
				.where(Encounter.IDENTIFIER.hasSystemWithAnyCode(SYSTEM_CODE_COVIDCARE_AU_APP_ENCOUNTER))
				.returnBundle(Bundle.class).execute();
		List<FHTEncounter> newFhtEncounters = new ArrayList<FHTEncounter>();
		bundle.getEntry().forEach(entry -> {
			if (entry.getResource() instanceof Encounter) {
				Encounter encounter = (Encounter) entry.getResource();
				Patient patient=(Patient)encounter.getSubject().getResource();
				//Encounters add to the table only doesn't exist & only patient selected from the organization. 
				if (!encounterRepository.existsByFhirID(encounter.getIdElement().getIdPart())
						&& patient.getIdElement().getIdPart().equals(fhtPatient.getFhirID())) {
					FHTEncounter fhtEncounter = new FHTEncounter();
					fhtEncounter.setCheckInID(encounter.getIdElement().getIdPart())
							.setCheckinDateTime(encounter.getPeriod().getEnd())
							.setFhtpatient(fhtPatient)
							.setFhirID(encounter.getIdElement().getIdPart())
							.setCovidcareID(patient.getIdentifier().get(0).getValue())
							.setReasonOrPromptedBy(encounter.getReasonCode().get(0).getCoding().get(0).getDisplay())
							.setFhtAlerts(getAlertsByEncounterID(encounter.getIdElement().getIdPart(),fhtEncounter));
					newFhtEncounters.add(fhtEncounter);
				}
			}
		});
		System.out.println(newFhtEncounters.toString());
		encounterRepository.saveAll(newFhtEncounters);
	}
	
	/**
	 * Get detected issue and set the alert types.
	 * 
	 * @param id
	 * @return
	 */
	private List<FHTAlert> getAlertsByEncounterID(String id,FHTEncounter fhtEncounter) {
		Bundle bundle = client.search().forResource(DetectedIssue.class)
				.where(new ReferenceClientParam("implicated").hasId(id))
				.returnBundle(Bundle.class).execute();
		List<FHTAlert> fhtAlerts = new ArrayList<FHTAlert>();
		bundle.getEntry().forEach(entry -> {
			if (entry.getResource() instanceof DetectedIssue) {
				DetectedIssue detectedIssue = (DetectedIssue) entry.getResource();
				// System.out.println(detectedIssue.getId());
				FHTAlert fhtAlert = new FHTAlert();
				// set code
				if (detectedIssue.getCode().getCoding().get(0).getCode() != null) {
					fhtAlert.setAlertCode(detectedIssue.getCode().getCoding().get(0).getCode());
				}
				// set identified datetime
				if (detectedIssue.getIdentifiedDateTimeType() != null) {
					fhtAlert.setEffectiveDateTime(detectedIssue.getIdentifiedDateTimeType().getValue());
				}
				// Severity
				if (detectedIssue.getSeverity() != null) {
					fhtAlert.setSeverity(detectedIssue.getSeverity().getDisplay());
				}
				// recommendation
				if (detectedIssue.getCode().getCoding().get(0).getDisplay() != null) {
					fhtAlert.setRecommendation(detectedIssue.getCode().getCoding().get(0).getDisplay());
				}
				// alert
				if (detectedIssue.getCode().getText() != null) {
					fhtAlert.setAlert(detectedIssue.getCode().getText());
				}
				AlertType alertType = null;
				if (isAlertBasedOnMental(detectedIssue.getCode().getCoding().get(0).getCode())) {
					alertType = alertTypeRepository.findByName(FHTConstants.MENTAL_HEALTH);
				} else {
					alertType = alertTypeRepository.findByName(FHTConstants.VITAL_SIGNS);
				}
				// System.out.println(alertType);
				fhtAlert.setAlertType(alertType);
				fhtAlert.setFhtEncounter(fhtEncounter);
				fhtAlerts.add(fhtAlert);
			}
		});
		return fhtAlerts;
	}
	
	/**
	 * return alert according to name
	 * @param fhtAlerts
	 * @param typeName
	 * @return
	 */
	private FHTAlert returnFHTAlertByTypeFromList(List<FHTAlert> fhtAlerts,String typeName) {
		FHTAlert ret_fhtAlert = null;
		for (FHTAlert fhtAlert : fhtAlerts) {
			if(fhtAlert.getAlertType().getName().equals(typeName)) {
				ret_fhtAlert= fhtAlert;
			}
		}
		return ret_fhtAlert;
	}

	private boolean isAlertBasedOnMental(String alertcode) {
		return alertcode.contains(FHTConstants.PREFIX_MENTAL);
	}

}
