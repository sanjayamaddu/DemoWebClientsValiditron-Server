package au.unimelb.covidcare.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.hl7.fhir.r4.model.Address.AddressType;
import org.hl7.fhir.r4.model.Address.AddressUse;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DetectedIssue;
import org.hl7.fhir.r4.model.DetectedIssue.DetectedIssueSeverity;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import au.unimelb.covidcare.model.CodingDetail;
import au.unimelb.covidcare.model.CovidDetectedIssue;
import au.unimelb.covidcare.model.CovidEncounter;
import au.unimelb.covidcare.model.CovidEncounterDisplayRow;
import au.unimelb.covidcare.model.CovidObservation;
import au.unimelb.covidcare.model.CovidOrganization;
import au.unimelb.covidcare.model.CovidPatient;
import au.unimelb.covidcare.model.ValueCodeableConcept;
import au.unimelb.covidcare.model.ValueQuantity;
import au.unimelb.covidcare.util.CovidConstants;
import au.unimelb.covidcare.util.EnumAlertCode;
import au.unimelb.covidcare.util.EnumFieldOrCheckInPROM;
import au.unimelb.covidcare.util.EnumValueCodeableConceptCode;
import au.unimelb.covidcare.util.EnumValueCodeableConceptSecondSet;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;

/**
 * Public controller handing HTTP request types POST,GET 
 * @author smaddumarach
 *
 */
@RestController
@RequestMapping("/api")
public class CovidCareController {
	

	private final static Logger LOGGER = Logger.getLogger(CovidCareController.class.getName());
	
	
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
	
	private IGenericClient client;
	
	@PostConstruct
	private void createFhirContext() {
		FhirContext ctx = FhirContext.forR4();
		IClientInterceptor authInterceptor = new BasicAuthInterceptor(username,password);
		IGenericClient client = ctx.newRestfulGenericClient(serverBase);
		client.registerInterceptor(authInterceptor);
		this.client=client;
		
	}

	/**[USERD IN THE WEB CLIENT]
	 * Register a patient with his observations(part of)
	 * 
	 * @param covidPatient
	 */
	@PostMapping("/postpatient")
	public void registerPatientWithObservations(@RequestBody CovidPatient covidPatient) {
		Patient patient = new Patient();
		patient.addIdentifier().setSystem(SYSTEM_CODE_COVIDCARE_AU_APP_PATIENT).setValue(covidPatient.getCovidCareId());
		patient.addIdentifier().setSystem("http://covidcare.au/app/prescription").setValue("");
		patient.addName().addGiven(covidPatient.getName());
		patient.setGender(Enumerations.AdministrativeGender.valueOf(covidPatient.getGender().toUpperCase()));
		patient.addExtension(new Extension("http://validitron.unimelb.edu.au/fhir/StructureDefinition/age")
				.setValue(new StringType(covidPatient.getAge().toString())));
		patient.addTelecom().setUse(ContactPoint.ContactPointUse.MOBILE)
				.setSystem(ContactPoint.ContactPointSystem.PHONE).setValue(covidPatient.getMobile());
		patient.addTelecom().setUse(ContactPoint.ContactPointUse.HOME).setSystem(ContactPoint.ContactPointSystem.EMAIL)
				.setValue(covidPatient.getEmail());
		patient.addAddress().setUse(AddressUse.HOME).setType(AddressType.POSTAL)
				.setPostalCode(covidPatient.getPostcode()).setCountry("AU");
		//This will be the clinical id of the patient.
		patient.addGeneralPractitioner().setReference("Organization/"+covidPatient.getCovidOrganization().getReferralClinicID());
		patient.setId(IdType.newRandomUuid());
		Bundle bundle = new Bundle();
		bundle.setType(Bundle.BundleType.TRANSACTION);
		// set patient to bundle
		bundle.addEntry().setFullUrl(patient.getIdElement().getValue()).setResource(patient).getRequest()
				.setUrl("Patient").setIfNoneExist("identifier=http://acme.org.cv/mrns")
				.setMethod(Bundle.HTTPVerb.POST);
		// create Observation list and bundle them with patient.
		createObservationsListAndBundle(covidPatient.getCovidObservationlst(), bundle, patient);
		Bundle resp = client.transaction().withBundle(bundle).execute();

	}

	/**[USERD IN THE WEB CLIENT]
	 * 
	 * Get all the registered patients with out observations.
	 * @return
	 */
	@RequestMapping(value="/getpatients",method = RequestMethod.GET)
	public List<CovidPatient> getAllPatients() {
		Bundle bundle = client.search().forResource(Patient.class)
				.include(Patient.INCLUDE_GENERAL_PRACTITIONER)
				.where(Patient.IDENTIFIER.hasSystemWithAnyCode(SYSTEM_CODE_COVIDCARE_AU_APP_PATIENT))
				.returnBundle(Bundle.class).execute();
		List<CovidPatient> listPatients = new ArrayList<CovidPatient>();
		bundle.getEntry().forEach(entry -> {
			if (entry.getResource() instanceof Patient) {
				Patient patient = (Patient) entry.getResource();
				CovidOrganization covidOrganization=new CovidOrganization();
				if(patient.getGeneralPractitioner()!=null && !patient.getGeneralPractitioner().isEmpty() ) {
					Organization organization=(Organization)patient.getGeneralPractitioner().get(0).getResource();
					covidOrganization.setReferralClinicID(organization.getIdElement().getIdPart()).setReferralClinicName(organization.getName());
				}
				CovidPatient covidPatient = new CovidPatient(covidOrganization,patient.getIdentifier().get(0).getValue());
				covidPatient
				.setName(patient.getName().get(0).getGivenAsSingleString())
				.setGender(patient.getGender().getDisplay())
				.setAge(Integer.parseInt((patient.getExtension()!=null && !patient.getExtension().isEmpty())?patient.getExtension().get(0).getValue().toString():"0"))
				.setEmail(patient.getTelecom().get(1).getValue())
				.setMobile(patient.getTelecom().get(0).getValue())
				.setPostcode(patient.getAddress().get(0).getPostalCode())
				//not 100% sure
				.setRegisterdDate(patient.getMeta().getLastUpdated());
				listPatients.add(covidPatient);
				LOGGER.info("FHIR ID:"+patient.getIdElement().getIdPart()+"CovidcareID"+covidPatient.getCovidCareId());
			}
		});
		Collections.sort(listPatients);
		LOGGER.info(listPatients.toString());
		return listPatients;
	}
	
	/**
	 * [HAS TO USERD IN THE WEB CLIENT]
	 * Get all covidcareids the registered patients in a clinic(referral).
	 * @param referralClinicId
	 * @return
	 */
	@RequestMapping(value="/getcovidcareids",method = RequestMethod.GET)
	public List<String> getAllCovidcareIDs() {
		Bundle bundle = client.search().forResource(Patient.class)
				.where(Patient.IDENTIFIER.hasSystemWithAnyCode(SYSTEM_CODE_COVIDCARE_AU_APP_PATIENT))
				//.and(Patient.GENERAL_PRACTITIONER.hasId(referralClinicId))
				.returnBundle(Bundle.class).execute();
		List<String> covidcareidlst = new ArrayList<String>();
		bundle.getEntry().forEach(entry -> {
			if (entry.getResource() instanceof Patient) {
				Patient patient = (Patient) entry.getResource();
				covidcareidlst.add(patient.getIdentifier().get(0).getValue());
			}
		});
		LOGGER.info(covidcareidlst.toString());
		return covidcareidlst;
	}
	

	/**
	 * [USERD IN THE WEB CLIENT]
	 * Get all referrals or clinics.
	 * @return
	 */
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
	 * [USERD IN THE WEB CLIENT]
	 * Get particular patient passing the referralID & covidcareID.
	 * With all the observations. 
	 * 
	 * @param covidcareid
	 * @return
	 */
	@GetMapping("/getpatientbyreferralandcovidcareid/{referralClinicId}/{covidcareid}")
	public CovidPatient getPatientByReferralAndCovidCareID(@PathVariable String referralClinicId,@PathVariable String covidcareid) {
		Bundle bundle = client.search().forResource(Patient.class)
				.include(Patient.INCLUDE_GENERAL_PRACTITIONER)
				.where(Patient.IDENTIFIER.exactly().systemAndIdentifier(SYSTEM_CODE_COVIDCARE_AU_APP_PATIENT, covidcareid))
				.and(Patient.GENERAL_PRACTITIONER.hasId(referralClinicId))
				.returnBundle(Bundle.class).execute();
		BundleEntryComponent bundleEntryComponent = bundle.getEntry().get(0);
		Patient patient = (Patient) bundleEntryComponent.getResource();
		CovidOrganization covidOrganization=new CovidOrganization();
		if(patient.getGeneralPractitioner()!=null && !patient.getGeneralPractitioner().isEmpty() ) {
			Organization organization=(Organization)patient.getGeneralPractitioner().get(0).getResource();
			covidOrganization.setReferralClinicID(organization.getIdElement().getIdPart()).setReferralClinicName(organization.getName());
		}
		String fhirID=patient.getIdElement().getIdPart();
		CovidPatient covidPatient = new CovidPatient(covidOrganization,covidcareid);
				covidPatient
				.setName(patient.getName().get(0).getGivenAsSingleString())
				.setGender(patient.getGender().getDisplay())
				.setAge(Integer.parseInt(patient.getExtension().get(0).getValue().toString()))
				.setEmail(patient.getTelecom().get(1).getValue())
				.setMobile(patient.getTelecom().get(0).getValue())
				.setPostcode(patient.getAddress().get(0).getPostalCode())
				//not 100% sure
				.setRegisterdDate(patient.getMeta().getLastUpdated());
		//set the observations
		covidPatient.setCovidObservationlst(getObservationsByPatientID(fhirID));
		LOGGER.info("FHIRID:"+patient.getIdElement().getIdPart()+"-"+covidPatient.toString());		
		return covidPatient;
			
	}
	
	/**
	 *  [USERD IN THE WEB CLIENT]
	 * create Observation list with encounter for existing patient.
	 * @param covidEncounter
	 */
	@PostMapping("/postencounterobservationsanddetectedissues")
	public void createEncounterObservationAndDetectedIssuesforExistingPatient(@RequestBody CovidEncounter covidEncounter) {
		
		Bundle bundlesearch = client
				.search().forResource(Patient.class).where(Patient.IDENTIFIER.exactly()
						.systemAndIdentifier(SYSTEM_CODE_COVIDCARE_AU_APP_PATIENT, covidEncounter.getCovidcareID()))
				.returnBundle(Bundle.class).execute();
		BundleEntryComponent bundleEntryComponent = bundlesearch.getEntry().get(0);
		Patient patient = (Patient) bundleEntryComponent.getResource();
		//Create encounter with patient
  		Encounter encounter=createEncounter(patient.getIdElement().getIdPart(), covidEncounter);
		Bundle bundlesave = new Bundle();
		bundlesave.setType(Bundle.BundleType.TRANSACTION);
		createObservationsListAndBundleWithEncounter(covidEncounter.getCovidObservationlst(),bundlesave,patient,encounter);
		createDetectedIssuesListAndBundleWithEncounter(covidEncounter.getCovidDetectedIssuelst(), bundlesave, patient,encounter);
		Bundle resp = client.transaction().withBundle(bundlesave).execute();
		
	}
	
	/**
	 * [USERD IN THE WEB CLIENT]
	 * Get all encounter list
	 * @return
	 */
	@RequestMapping(value="/getencounterlistasdisplayrow",method = RequestMethod.GET)
	public List<CovidEncounterDisplayRow> getEncounterListAsDisplaRow() {
		Bundle bundle = client.search().forResource(Encounter.class)
				.include(Encounter.INCLUDE_SUBJECT)
				.where(Patient.IDENTIFIER.hasSystemWithAnyCode(SYSTEM_CODE_COVIDCARE_AU_APP_ENCOUNTER))
				.returnBundle(Bundle.class).execute();
		List<CovidEncounterDisplayRow> covidEncounterDisplayRows = new ArrayList<CovidEncounterDisplayRow>();
		bundle.getEntry().forEach(entry -> {
			if (entry.getResource() instanceof Encounter) {
				Encounter encounter = (Encounter) entry.getResource();
				Patient patient=(Patient)encounter.getSubject().getResource();
				CovidEncounterDisplayRow coviEncounterDisplayRow = new CovidEncounterDisplayRow();
				coviEncounterDisplayRow
				.setCheckInID(encounter.getIdElement().getIdPart())
				.setCheckinDateTime(encounter.getPeriod().getEnd())
				.setReasonOrPromptedBy((encounter.getReasonCode()!=null && !encounter.getReasonCode().isEmpty())?encounter.getReasonCode().get(0).getCoding().get(0).getDisplay():"")
				.setCovidcareID(patient.getIdentifier().get(0).getValue())
				.setPatientName(patient.getName().get(0).getGivenAsSingleString());
				List<CovidDetectedIssue> covidDetectedIssues=getDetectedIssuesByEncounterID(encounter.getIdElement().getIdPart());
				for (CovidDetectedIssue covidDetectedIssue : covidDetectedIssues) {
					if(isAlertBasedOnMental(covidDetectedIssue.getName())){
						coviEncounterDisplayRow.setMentalHealthAlert(covidDetectedIssue.getName());
					}else {
						coviEncounterDisplayRow.setVitalSignAlert(covidDetectedIssue.getName());
					}
				}
				covidEncounterDisplayRows.add(coviEncounterDisplayRow);
			}
		});
		Collections.sort(covidEncounterDisplayRows);
		LOGGER.info(covidEncounterDisplayRows.toString());
		return covidEncounterDisplayRows;
	}
	/**
	 * [USERD IN THE WEB CLIENT]
	 * 
	 * Get Encounter by res id.
	 * @param resID
	 * @return
	 */
	@GetMapping("/getencounterbyresourceid/{resID}")
	public CovidEncounter getEncounterByResID(@PathVariable("resID") String resID) {
		Bundle bundle = client.search().forResource(Encounter.class)
				.include(Encounter.INCLUDE_PATIENT)
				.where(Encounter.IDENTIFIER.hasSystemWithAnyCode(SYSTEM_CODE_COVIDCARE_AU_APP_ENCOUNTER))
				.and(Encounter.RES_ID.exactly().code(resID))
				.returnBundle(Bundle.class).execute();
		BundleEntryComponent bundleEntryComponent = bundle.getEntry().get(0);
		Encounter encounter = (Encounter) bundleEntryComponent.getResource();
		Patient patient=(Patient)encounter.getSubject().getResource();
		CovidEncounter covidEncounter = new CovidEncounter();
				covidEncounter
				.setCheckInID(encounter.getIdElement().getIdPart())
				.setCheckinDateTime(encounter.getPeriod().getEnd())
				.setCovidcareID(patient.getIdentifier().get(0).getValue())
				.setReasonOrPromptedBy(encounter.getReasonCode().get(0).getCoding().get(0).getDisplay())
				.setCovidObservationlst(getObservationsByEncounterID(encounter.getIdElement().getIdPart()))
				.setCovidDetectedIssuelst(getDetectedIssuesByEncounterID(encounter.getIdElement().getIdPart()));
		LOGGER.info(covidEncounter.toString());
		return covidEncounter;
	}
	
	
		/**
	 * Create the encounter with the fhirPatient ID.
	 * @param fhirPatientId
	 * @param covidEncounter
	 * @return
	 */
	private Encounter createEncounter(String fhirPatientId,CovidEncounter covidEncounter) {
		//Encounter
		Encounter encounter=new Encounter();
		String randomValue=IdType.newRandomUuid().asStringValue();
		encounter.addIdentifier().setSystem(SYSTEM_CODE_COVIDCARE_AU_APP_ENCOUNTER).setValue(randomValue);
		//No Need have patient
//		//Keep the covid care id
//		encounter.addIdentifier().setSystem("http://covidcare.au/app/patient").setValue(covidEncounter.getCovidcareID());
		encounter.setSubject(new Reference().setReference("Patient/"+fhirPatientId));
		encounter.setPeriod(new Period().setEnd(covidEncounter.getCheckinDateTime()));
		encounter.addReasonCode(new CodeableConcept(new Coding().setSystem("http://snomed.info/sct").setCode(CovidConstants.getCode(covidEncounter.getReasonOrPromptedBy()))
				.setDisplay(CovidConstants.getCodeText(covidEncounter.getReasonOrPromptedBy()))));
		Bundle bundle = new Bundle();
		bundle.setType(Bundle.BundleType.TRANSACTION);
		// set patient to bundle
		bundle.addEntry().setFullUrl(encounter.getIdElement().getValue()).setResource(encounter).getRequest()
				.setUrl("Encounter").setIfNoneExist("identifier=http://acme.org.cv/mrns")
				.setMethod(Bundle.HTTPVerb.POST);
		Bundle resp = client.transaction().withBundle(bundle).execute();
	    //search from the saved encounter.
	    //there may be better way to get the encounter id just after saved 
	    Bundle bundlesearch = client
				.search().forResource(Encounter.class).where(Encounter.IDENTIFIER.exactly()
						.systemAndIdentifier(SYSTEM_CODE_COVIDCARE_AU_APP_ENCOUNTER, randomValue))
				.returnBundle(Bundle.class).execute();
		BundleEntryComponent bundleEntryComponent = bundlesearch.getEntry().get(0);
		Encounter persisted_encounter= (Encounter) bundleEntryComponent.getResource();
		return persisted_encounter;		
	}

	/**
	 * Get observation by patient id.
	 * @param id
	 * @return
	 */
	private List<CovidObservation> getObservationsByPatientID(String id) {
		Bundle bundle = client.search().forResource(Observation.class).where(Observation.PATIENT.hasId(id))
				.returnBundle(Bundle.class).execute();
		List<CovidObservation> covidObservationLst = new ArrayList<CovidObservation>();
		bundle.getEntry().forEach(entry -> {
			if (entry.getResource() instanceof Observation) {
				CovidObservation covidObservation = new CovidObservation();
				Observation observation = (Observation) entry.getResource();
				// set identified datetime
				if (observation.getEffectiveDateTimeType() != null) {
					covidObservation.setEffectiveDateTime(observation.getEffectiveDateTimeType().getValue());
				}
				// set name
				if (observation.getCode().getCoding().get(0).getDisplay() != null) {
					covidObservation
							.setName(getEnumFromNameCheckInPROM(observation.getCode().getCoding().get(0).getDisplay()).toString());
				}
				// set value
				if (observation.hasValueQuantity()) {
					covidObservation.setValue(observation.getValueQuantity().getValue().toString());
				} else if (observation.hasValueCodeableConcept()) {
					covidObservation.setValue(observation.getValueCodeableConcept().getCoding().get(0).getDisplay());
				} else {

				}
				covidObservationLst.add(covidObservation);
			}
		});
		return covidObservationLst;
	}
	/**
	 * Get observations by encounter id.
	 * @param id
	 * @return
	 */
	private List<CovidObservation> getObservationsByEncounterID(String id) {
		Bundle bundle = client.search().forResource(Observation.class).where(Observation.ENCOUNTER.hasId(id))
				.returnBundle(Bundle.class).execute();
		List<CovidObservation> covidObservationLst = new ArrayList<CovidObservation>();
		bundle.getEntry().forEach(entry -> {
			if (entry.getResource() instanceof Observation) {
				CovidObservation covidObservation = new CovidObservation();
				Observation observation = (Observation) entry.getResource();
				// set identified datetime
				if (observation.getEffectiveDateTimeType() != null) {
					covidObservation.setEffectiveDateTime(observation.getEffectiveDateTimeType().getValue());
				}
				// set name
				if (observation.getCode().getCoding().get(0).getDisplay() != null) {
					covidObservation
							.setName(getEnumFromNameCheckInPROM(observation.getCode().getCoding().get(0).getDisplay()).toString());
				}
				// set value
				if (observation.hasValueQuantity()) {
					covidObservation.setValue(observation.getValueQuantity().getValue().toString());
				} else if (observation.hasValueCodeableConcept()) {
					covidObservation.setValue(observation.getValueCodeableConcept().getCoding().get(0).getDisplay());
				} else {

				}
				covidObservationLst.add(covidObservation);
			}
		});
		return covidObservationLst;
	}
	/**
	 * Get detected issues by encounter id.
	 * @param id
	 * @return
	 */
	private List<CovidDetectedIssue> getDetectedIssuesByEncounterID(String id) {
		Bundle bundle = client.search().forResource(DetectedIssue.class)
				.where(new ReferenceClientParam("implicated").hasId(id))
				.returnBundle(Bundle.class).execute();
		List<CovidDetectedIssue> covidDetectedIssuesLst = new ArrayList<CovidDetectedIssue>();
		bundle.getEntry().forEach(entry -> {
			if (entry.getResource() instanceof DetectedIssue) {
				CovidDetectedIssue covidDetectedIssue = new CovidDetectedIssue();
				DetectedIssue detectedIssue = (DetectedIssue) entry.getResource();
				// set identified datetime
				if (detectedIssue.getIdentifiedDateTimeType() != null) {
					covidDetectedIssue.setEffectiveDateTime(detectedIssue.getIdentifiedDateTimeType().getValue());
				}
				// set name
				if (detectedIssue.getCode().getCoding().get(0).getDisplay() != null) {
					covidDetectedIssue.setName(getEnumFromNameAlertCode(detectedIssue.getCode().getCoding().get(0).getDisplay()).toString());
				}
				//set Text
				if (detectedIssue.getCode().getText()!= null) {
					covidDetectedIssue.setText(detectedIssue.getCode().getText());;
				}
				//set severity
				if(detectedIssue.getSeverity()!=null) {
					covidDetectedIssue.setSeverity(detectedIssue.getSeverity().toString());
				}
				if(detectedIssue.getStatus()!=null) {
					covidDetectedIssue.setStatus(detectedIssue.getStatus().toString());
				}
				covidDetectedIssuesLst.add(covidDetectedIssue);
			}
		});

		return covidDetectedIssuesLst;
	}
		

	/**
	 * create observation list in a bundle without encounter.
	 * 
	 * @param covidObservationsLst
	 * @param bundle
	 * @param patient
	 */
	private void createObservationsListAndBundle(List<CovidObservation> covidObservationsLst, Bundle bundle,
			Patient patient) {
		for (CovidObservation covidObservation : covidObservationsLst) {
			// Create an observation object
			Observation observation = new Observation();
			observation.setStatus(Observation.ObservationStatus.FINAL);
			observation.setEffective(new DateTimeType(covidObservation.getEffectiveDateTime()));
			// pick the correct codingDetails & value Details/value Codeable Concept
			populateCodingDetailsAndValuDetailsWithEnumFieldOrCheckInPROM(covidObservation);
			// coding
			observation.getCode().addCoding().setSystem(covidObservation.getCodingDetail().getSystem())
					.setCode(covidObservation.getCodingDetail().getCode())
					.setDisplay(covidObservation.getCodingDetail().getDisplay());
			// value Details
			if (covidObservation.getValueQuantity().getValue() != null) {
				observation.setValue(new Quantity().setValue(covidObservation.getValueQuantity().getValue())
						.setUnit(covidObservation.getValueQuantity().getUnit())
						.setSystem(covidObservation.getValueQuantity().getSystem())
						.setCode(covidObservation.getValueQuantity().getCode()));
			} else if (covidObservation.getValueCodeableConcept().getText() != null) {
				Coding coding = new Coding().setSystem(covidObservation.getValueCodeableConcept().getSystem())
						.setCode(covidObservation.getValueCodeableConcept().getCode())
						.setDisplay(covidObservation.getValueCodeableConcept().getText());
				observation.setValue(new CodeableConcept().addCoding(coding));
			} else {

			}
			observation.setSubject(new Reference().setReference("Patient/"+patient.getIdElement().getIdPart()));
			bundle.addEntry().setResource(observation).getRequest().setUrl("Observation")
					.setMethod(Bundle.HTTPVerb.POST);
		}
	}
	/**
	 * create observation list in a bundle with encounter.
	 * @param covidObservationsLst
	 * @param bundle
	 * @param patient
	 * @param encounter
	 */
	private void createObservationsListAndBundleWithEncounter(List<CovidObservation> covidObservationsLst, Bundle bundle,
			Patient patient,Encounter encounter) {
		for (CovidObservation covidObservation : covidObservationsLst) {
			// Create an observation object
			Observation observation = new Observation();
			observation.setStatus(Observation.ObservationStatus.FINAL);
			observation.setEffective(new DateTimeType(covidObservation.getEffectiveDateTime()));
			// pick the correct codingDetails & value Details/value Codeable Concept
			populateCodingDetailsAndValuDetailsWithEnumFieldOrCheckInPROM(covidObservation);
			// coding
			observation.getCode().addCoding().setSystem(covidObservation.getCodingDetail().getSystem())
					.setCode(covidObservation.getCodingDetail().getCode())
					.setDisplay(covidObservation.getCodingDetail().getDisplay());
			// value Details
			if (covidObservation.getValueQuantity().getValue() != null) {
				observation.setValue(new Quantity().setValue(covidObservation.getValueQuantity().getValue())
						.setUnit(covidObservation.getValueQuantity().getUnit())
						.setSystem(covidObservation.getValueQuantity().getSystem())
						.setCode(covidObservation.getValueQuantity().getCode()));
			} else if (covidObservation.getValueCodeableConcept().getText() != null) {
				Coding coding = new Coding().setSystem(covidObservation.getValueCodeableConcept().getSystem())
						.setCode(covidObservation.getValueCodeableConcept().getCode())
						.setDisplay(covidObservation.getValueCodeableConcept().getText());
				observation.setValue(new CodeableConcept().addCoding(coding));
			} else {

			}
			observation.setSubject(new Reference().setReference("Patient/"+patient.getIdElement().getIdPart()));
			observation.setEncounter(new Reference().setReference("Encounter/"+encounter.getIdElement().getIdPart()));
			bundle.addEntry().setResource(observation).getRequest().setUrl("Observation")
					.setMethod(Bundle.HTTPVerb.POST);
		}
	}


	/**
	 * Add coding details & value details to the covid observation object by picking
	 * the correct enum values.
	 * 
	 * @param covidObservation
	 * @return
	 */
	private CovidObservation populateCodingDetailsAndValuDetailsWithEnumFieldOrCheckInPROM(
			CovidObservation covidObservation) {

		EnumFieldOrCheckInPROM covidCareFieldOrCheckInPROM = EnumFieldOrCheckInPROM.valueOf(covidObservation.getName());
		CodingDetail codingDetail = new CodingDetail();
		ValueQuantity valueQuantity = new ValueQuantity();
		ValueCodeableConcept valueCodeableConcept = new ValueCodeableConcept();
		switch (covidCareFieldOrCheckInPROM) {
		//PROMS
		case HEART_RATE:
			codingDetail.setSystem("http://loinc.org");
			codingDetail.setCode("8867-4");
			codingDetail.setDisplay("Heart rate");
			valueQuantity.setValue(new BigDecimal(covidObservation.getValue()));
			valueQuantity.setUnit("beats/min");
			valueQuantity.setSystem("http://unitsofmeasure.org");
			valueQuantity.setCode("/min");
			break;
		case RESPIRATORY_RATE:
			codingDetail.setSystem("http://loinc.org");
			codingDetail.setCode("9279-1");
			codingDetail.setDisplay("Respiratory rate");
			valueQuantity.setValue(new BigDecimal(covidObservation.getValue()));
			valueQuantity.setUnit("beats/min");
			valueQuantity.setSystem("http://unitsofmeasure.org");
			valueQuantity.setCode("/min");
			break;
		case SINGLE_BREATH_COUNT:
			codingDetail.setSystem("http://loinc.org");
			codingDetail.setCode("60821-6");
			codingDetail.setDisplay("Apnea duration");
			valueQuantity.setValue(new BigDecimal(covidObservation.getValue()));
			valueQuantity.setUnit("sec");
			valueQuantity.setSystem("http://unitsofmeasure.org");
			valueQuantity.setCode("sec");
			break;
		case TEMPERATURE:
			codingDetail.setSystem("http://loinc.org");
			codingDetail.setCode("8310-5");
			codingDetail.setDisplay("Body temperature");
			valueQuantity.setValue(new BigDecimal(covidObservation.getValue()));
			valueQuantity.setUnit("°C");
			valueQuantity.setSystem("http://unitsofmeasure.org");
			valueQuantity.setCode("Cel");
			break;
		case OXYGEN_SATURATION:
			codingDetail.setSystem("http://loinc.org");
			codingDetail.setCode("2708-6");
			codingDetail.setDisplay("Oxygen saturation in Arterial blood");
			valueQuantity.setValue(new BigDecimal(covidObservation.getValue()));
			valueQuantity.setUnit("%");
			valueQuantity.setSystem("http://unitsofmeasure.org");
			valueQuantity.setCode("%");
			break;
		//Value codeable concept starts	
		case DRY_COUGH:
			codingDetail.setSystem("http://snomed.info/sct");
			codingDetail.setCode("11833005");
			codingDetail.setDisplay("Dry cough");
			valueCodeableConcept.setSystem("http://snomed.info/sct");
			valueCodeableConcept.setCode(EnumValueCodeableConceptCode.valueOf(covidObservation.getValue()).getName());
			valueCodeableConcept.setText(covidObservation.getValue().toLowerCase());
			break;	
		case FEVER:
			codingDetail.setSystem("http://snomed.info/sct");
			codingDetail.setCode("386661006");
			codingDetail.setDisplay("Fever");
			valueCodeableConcept.setSystem("http://snomed.info/sct");
			valueCodeableConcept.setCode(EnumValueCodeableConceptCode.valueOf(covidObservation.getValue()).getName());
			valueCodeableConcept.setText(covidObservation.getValue().toLowerCase());
			break;
		case FATIGUE:
			codingDetail.setSystem("http://snomed.info/sct");
			codingDetail.setCode("84229001");
			codingDetail.setDisplay("Fatigue");
			valueCodeableConcept.setSystem("http://snomed.info/sct");
			valueCodeableConcept.setCode(EnumValueCodeableConceptCode.valueOf(covidObservation.getValue()).getName());
			valueCodeableConcept.setText(covidObservation.getValue().toLowerCase());
			break;
		case SHORTNESS_OF_BREATH:
			codingDetail.setSystem("http://snomed.info/sct");
			codingDetail.setCode("267036007");
			codingDetail.setDisplay("Dyspnea");
			valueCodeableConcept.setSystem("http://snomed.info/sct");
			valueCodeableConcept.setCode(EnumValueCodeableConceptCode.valueOf(covidObservation.getValue()).getName());
			valueCodeableConcept.setText(covidObservation.getValue().toLowerCase());
			break;	
		case LACK_OF_SMELL_OR_TASTE:
			codingDetail.setSystem("http://snomed.info/sct");
			codingDetail.setCode("44169009");
			codingDetail.setDisplay("Loss of sense of smell");
			valueCodeableConcept.setSystem("http://snomed.info/sct");
			valueCodeableConcept.setCode(EnumValueCodeableConceptCode.valueOf(covidObservation.getValue()).getName());
			valueCodeableConcept.setText(covidObservation.getValue().toLowerCase());
			break;
		case APATHETIC:
			codingDetail.setSystem("http://snomed.info/sct");
			codingDetail.setCode("20602000");
			codingDetail.setDisplay("Indifference");
			valueCodeableConcept.setSystem("http://covidcare.au/app/codes");
			valueCodeableConcept.setCode(covidObservation.getValue().toLowerCase());
			valueCodeableConcept.setText(EnumValueCodeableConceptSecondSet.valueOf(covidObservation.getValue()).getDisplay());
			break;
		case DEPRESSED:
			codingDetail.setSystem("http://snomed.info/sct");
			codingDetail.setCode("366979004");
			codingDetail.setDisplay("Depressed");
			valueCodeableConcept.setSystem("http://covidcare.au/app/codes");
			valueCodeableConcept.setCode(covidObservation.getValue().toLowerCase());
			valueCodeableConcept.setText(EnumValueCodeableConceptSecondSet.valueOf(covidObservation.getValue()).getDisplay());
			break;
		case ANXIOUS:
			codingDetail.setSystem("http://snomed.info/sct");
			codingDetail.setCode("48694002");
			codingDetail.setDisplay("Anxiety");
			valueCodeableConcept.setSystem("http://covidcare.au/app/codes");
			valueCodeableConcept.setCode(covidObservation.getValue().toLowerCase());
			valueCodeableConcept.setText(EnumValueCodeableConceptSecondSet.valueOf(covidObservation.getValue()).getDisplay());
			break;
		case WORRIED:
			codingDetail.setSystem("http://snomed.info/sct");
			codingDetail.setCode("79015004");
			codingDetail.setDisplay("Worried");
			valueCodeableConcept.setSystem("http://covidcare.au/app/codes");
			valueCodeableConcept.setCode(covidObservation.getValue().toLowerCase());
			valueCodeableConcept.setText(EnumValueCodeableConceptSecondSet.valueOf(covidObservation.getValue()).getDisplay());
			break;	
		//COVIDCare fields
		case HAS_LUNG_CONDITION:
			codingDetail.setSystem("http://snomed.info/sct");
			codingDetail.setCode("19829001");
			codingDetail.setDisplay("Disorder of lung");
			valueCodeableConcept.setSystem("http://terminology.hl7.org/CodeSystem/v2-0136");
			valueCodeableConcept.setCode(getFistLetterOftheString(covidObservation.getValue()));
			valueCodeableConcept.setText(covidObservation.getValue());
			break;		
		case HAS_CARDIOVASCULAR_CONDITION:
			codingDetail.setSystem("http://snomed.info/sct");
			codingDetail.setCode("49601007");
			codingDetail.setDisplay("Disorder of cardiovascular system");
			valueCodeableConcept.setSystem("http://terminology.hl7.org/CodeSystem/v2-0136");
			valueCodeableConcept.setCode(getFistLetterOftheString(covidObservation.getValue()));
			valueCodeableConcept.setText(covidObservation.getValue());
			break;
		case IS_SMOKER:
			codingDetail.setSystem("http://snomed.info/sct");
			codingDetail.setCode("77176002");
			codingDetail.setDisplay("Smoker");
			valueCodeableConcept.setSystem("http://terminology.hl7.org/CodeSystem/v2-0136");
			valueCodeableConcept.setCode(getFistLetterOftheString(covidObservation.getValue()));
			valueCodeableConcept.setText(covidObservation.getValue());
			break;	
		case HAS_DIABETES:
			codingDetail.setSystem("http://snomed.info/sct");
			codingDetail.setCode("73211009");
			codingDetail.setDisplay("Diabetes mellitus");
			valueCodeableConcept.setSystem("http://terminology.hl7.org/CodeSystem/v2-0136");
			valueCodeableConcept.setCode(getFistLetterOftheString(covidObservation.getValue()));
			valueCodeableConcept.setText(covidObservation.getValue());
			break;	
		case IS_OVERWEIGHT:
			codingDetail.setSystem("http://snomed.info/sct");
			codingDetail.setCode("238131007");
			codingDetail.setDisplay("Overweight");
			valueCodeableConcept.setSystem("http://terminology.hl7.org/CodeSystem/v2-0136");
			valueCodeableConcept.setCode(getFistLetterOftheString(covidObservation.getValue()));
			valueCodeableConcept.setText(covidObservation.getValue());
			break;	
		case HAS_HYPERTENSION:
			codingDetail.setSystem("http://snomed.info/sct");
			codingDetail.setCode("38341003");
			codingDetail.setDisplay("Hypertensive disorder, systemic arterial");
			valueCodeableConcept.setSystem("http://terminology.hl7.org/CodeSystem/v2-0136");
			valueCodeableConcept.setCode(getFistLetterOftheString(covidObservation.getValue()));
			valueCodeableConcept.setText(covidObservation.getValue());
			break;
		case IS_PREGNANT:
			codingDetail.setSystem("http://snomed.info/sct");
			codingDetail.setCode("77386006");
			codingDetail.setDisplay("Pregnant");
			valueCodeableConcept.setSystem("http://terminology.hl7.org/CodeSystem/v2-0136");
			valueCodeableConcept.setCode(getFistLetterOftheString(covidObservation.getValue()));
			valueCodeableConcept.setText(covidObservation.getValue());
			break;
		case CONTACT_OPT_OUT:
			codingDetail.setSystem("http://snomed.info/sct");
			codingDetail.setCode("contact-opt-out");
			codingDetail.setDisplay("Opt out of being contacted");
			valueCodeableConcept.setSystem("http://terminology.hl7.org/CodeSystem/v2-0136");
			valueCodeableConcept.setCode(getFistLetterOftheString(covidObservation.getValue()));
			valueCodeableConcept.setText(covidObservation.getValue());
			break;
		//field related to COVID19	
		case SYMPTOM_START_DATE:
			codingDetail.setSystem("http://snomed.info/sct");
			codingDetail.setCode("162434008");
			codingDetail.setDisplay("Time since symptom started");
			valueCodeableConcept.setText(covidObservation.getValue());
			break;
		case TEST_RESULT:
			codingDetail.setSystem("http://snomed.info/sct");
			codingDetail.setCode("1445431000168101");
			codingDetail.setDisplay("COVID-19 nucleic acid assay");
			valueCodeableConcept.setText(covidObservation.getValue());
			break;
		case TEST_DATE:
			codingDetail.setSystem("http://covidcare.au/app/codes");
			codingDetail.setCode("covid-test-date");
			codingDetail.setDisplay("Test date");
			valueCodeableConcept.setText(covidObservation.getValue());
			break;	
		}

		covidObservation.setCodingDetail(codingDetail);
		covidObservation.setValueQuantity(valueQuantity);
		covidObservation.setValueCodeableConcept(valueCodeableConcept);

		return covidObservation;
	}

	

	/**
	 * create Detected Issue list in a bundle.
	 * 
	 * @param covidObservationsLst
	 * @param bundle
	 * @param patient
	 */
	private void createDetectedIssuesListAndBundleWithEncounter(List<CovidDetectedIssue> covidDetectedIssuesLst, Bundle bundle,
			Patient patient,Encounter encounter) {
		for (CovidDetectedIssue covidDetectedIssue : covidDetectedIssuesLst) {
			// Create a detected issue object
			DetectedIssue detectedIssue = new DetectedIssue();
			detectedIssue.setStatus(DetectedIssue.DetectedIssueStatus.FINAL);
			detectedIssue.setIdentified(new DateTimeType(covidDetectedIssue.getEffectiveDateTime()));
			List<Reference> references=new ArrayList<Reference>();
			Reference ref=new Reference().setReference("Encounter/"+encounter.getIdElement().getIdPart());
			references.add(ref);
			detectedIssue.setImplicated(references);
			// pick the correct codingDetails & value Details/value Codeable Concept
			populateCodingDetailsWithEnumCovidCareAlertCode(covidDetectedIssue);
			// coding
			detectedIssue.getCode().addCoding().setSystem(covidDetectedIssue.getCodingDetail().getSystem())
					.setCode(covidDetectedIssue.getCodingDetail().getCode())
					.setDisplay(covidDetectedIssue.getCodingDetail().getDisplay());
			detectedIssue.getCode().setText(covidDetectedIssue.getText());
			detectedIssue.setSeverity(DetectedIssueSeverity.valueOf(covidDetectedIssue.getSeverity()));
			// The observation refers to the patient using the ID, which is already set to a
			// temporary UUID
			detectedIssue.setPatient(new Reference().setReference("Patient/"+patient.getIdElement().getIdPart()));
			bundle.addEntry().setResource(detectedIssue).getRequest().setUrl("DetectedIssue")
					.setMethod(Bundle.HTTPVerb.POST);
		}
	}

	/**
	 * 
	 * @param covidDetectedIssue
	 * @return
	 */
	private CovidDetectedIssue populateCodingDetailsWithEnumCovidCareAlertCode(CovidDetectedIssue covidDetectedIssue) {

		EnumAlertCode enumAlertCode = EnumAlertCode.valueOf(covidDetectedIssue.getName());
		CodingDetail codingDetail = new CodingDetail();
		codingDetail.setSystem("http://covidcare.au/app/codes");
		codingDetail.setCode(enumAlertCode.toString());
		codingDetail.setDisplay(enumAlertCode.getRecomendation());
		covidDetectedIssue.setText(enumAlertCode.getAlert());

		switch (enumAlertCode) {
		case immediateEmergency:
			covidDetectedIssue.setSeverity("HIGH");
			break;
		case immediateAbnormality:
			covidDetectedIssue.setSeverity("LOW");
			break;
		case trendNegative:
			covidDetectedIssue.setSeverity("LOW");
			break;
		case trendEmergency:
			covidDetectedIssue.setSeverity("HIGH");
			break;
		case safe:
			covidDetectedIssue.setSeverity("LOW");
			break;
		case mentalNegative:
			covidDetectedIssue.setSeverity("LOW");
			break;
		case mentalSafe:
			covidDetectedIssue.setSeverity("LOW");
			break;
		}
		covidDetectedIssue.setCodingDetail(codingDetail);
		return covidDetectedIssue;
	}

	/**
	 * get enum from the name.
	 * 
	 * @param name
	 * @return
	 */
	private EnumFieldOrCheckInPROM getEnumFromNameCheckInPROM(String name) {

		for (EnumFieldOrCheckInPROM enumFieldOrCheckInPROM : EnumFieldOrCheckInPROM.values()) {
			if (enumFieldOrCheckInPROM.getName().equals(name)) {
				return enumFieldOrCheckInPROM;
			}
		}
		return null;
	}
	/**
	 * identify enum from the recommendation
	 * @param name
	 * @return
	 */
	private EnumAlertCode getEnumFromNameAlertCode(String name) {

		for (EnumAlertCode enumAlertCode : EnumAlertCode.values()) {
			if (enumAlertCode.getRecomendation().equals(name)) {
				return enumAlertCode;
			}
		}
		return null;
	}
	/**
	 * return first letter from string (to get Y/N) 
	 * @return
	 */
	private String getFistLetterOftheString(String answer) {
		return answer.substring(0,1);
		
	}
	/**
	 * Check the alertcode for mental type
	 * @param alertcode
	 * @return
	 */
	private boolean isAlertBasedOnMental(String alertcode) {
		return alertcode.contains(CovidConstants.PREFIX_MENTAL);
	}

}
