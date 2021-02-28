package au.unimelb.covidcare.test;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import au.unimelb.covidcare.controller.CovidCareController;
import au.unimelb.covidcare.model.CovidDetectedIssue;
import au.unimelb.covidcare.model.CovidEncounter;
import au.unimelb.covidcare.model.CovidObservation;
import au.unimelb.covidcare.model.CovidOrganization;
import au.unimelb.covidcare.model.CovidPatient;

public class SimpleTestCovidCare {

	@Autowired
	CovidCareController controller;
	
	
	@BeforeMethod
	public void setUp() {
		controller = new CovidCareController();
		}	

	@AfterMethod
	public void tearDown() {
		controller = null;
	}
	
	@Test
	public void testCreatPatient() {
		CovidOrganization covidOrganization=new CovidOrganization();
		covidOrganization.setReferralClinicID("2152").setReferralClinicName("Kane Medical");
		CovidPatient covidPatient=new CovidPatient(covidOrganization,"PAT00011");
		//covidPatient.setCovidCareId("complete-data");
		covidPatient.setName("Sandra john sister")
		.setGender("FEMALE")
		.setAge(49)
		.setMobile("0401523654")
		.setEmail("myemail@testing.con.ad")
		.setPostcode("3214");
		CovidObservation covidObservation=new CovidObservation();
		covidObservation.setName("RESPIRATORY_RATE")
		.setValue("13.56");
		CovidObservation covidObservation1=new CovidObservation();
		covidObservation1.setName("HEART_RATE")
		.setValue("112.3");
		List<CovidObservation> covidobservationLst=new ArrayList<CovidObservation>();
		covidobservationLst.add(covidObservation);
		covidobservationLst.add(covidObservation1);
		covidPatient.setCovidObservationlst(covidobservationLst);
		controller.registerPatientWithObservations(covidPatient);
	}

	@Test
	public void testgetAllReferrals() {
		controller.getAllReferrals();
	}
	
	
	@Test
	public void testgetAllPatient() {
		controller.getAllPatients();
	}
	
	
	
	@Test
	public void testgetPatientByReferralIDAndCovidcareID() {
		controller.getPatientByReferralAndCovidCareID("2152","x");
	}
	
	@Test
	public void testcreateEncounter() {
		CovidEncounter encounter=new CovidEncounter();
		encounter.setCovidcareID("complete-data");
		encounter.setReasonOrPromptedBy("dashboard");
		encounter.setCheckinDateTime(new Date());
		//1
		CovidObservation covidObservation=new CovidObservation();
		covidObservation.setName("RESPIRATORY_RATE")
		.setValue("0.000003")
		.setEffectiveDateTime(new Date());
		//2
		CovidObservation covidObservation1=new CovidObservation();
		covidObservation1.setName("HEART_RATE")
		.setValue("0.000003")
		.setEffectiveDateTime(new Date());
		//3
		CovidObservation covidObservation2=new CovidObservation();
		covidObservation2.setName("LACK_OF_SMELL_OR_TASTE")
		.setValue("CONSTANT")
		.setEffectiveDateTime(new Date());
		//4
		CovidObservation covidObservation3=new CovidObservation();
		covidObservation3.setName("DEPRESSED")
		.setValue("NOTATALL")
		.setEffectiveDateTime(new Date());
		List<CovidObservation> covidobservationLst=new ArrayList<CovidObservation>();
		covidobservationLst.add(covidObservation);
		covidobservationLst.add(covidObservation1);
		covidobservationLst.add(covidObservation2);
		covidobservationLst.add(covidObservation3);
		encounter.setCovidObservationlst(covidobservationLst);
		//2 Detected issues
		//vital
		CovidDetectedIssue vitalcovidDetectedIssue=new CovidDetectedIssue();
		vitalcovidDetectedIssue.setName("trendEmergency");
		vitalcovidDetectedIssue.setEffectiveDateTime(new Date());
		//mental
		CovidDetectedIssue mentalCovidDetectedIssue=new CovidDetectedIssue();
		mentalCovidDetectedIssue.setName("mentalSafe");
		mentalCovidDetectedIssue.setEffectiveDateTime(new Date());
		
		List<CovidDetectedIssue> covidDetectedIssueLst=new ArrayList<CovidDetectedIssue>();
		covidDetectedIssueLst.add(vitalcovidDetectedIssue);
		covidDetectedIssueLst.add(mentalCovidDetectedIssue);
		encounter.setCovidDetectedIssuelst(covidDetectedIssueLst);
		controller.createEncounterObservationAndDetectedIssuesforExistingPatient(encounter);
	}
	
	@Test
	public void testgetEncounterListAsDisplayRows() {
		controller.getEncounterListAsDisplaRow();
	}
	
	
	
	@Test
	public void testgetAllcovidcareIds() {
		controller.getAllCovidcareIDs();
	}
	
	@Test
	public void testgetEncounterByResID() {
		controller.getEncounterByResID("1673");
	}
	
	/**
	 * Simulation for the check ins with encounters.
	 */
	@Test
	public void testcreateObservationsForPatientCOVIDCareID() {
		CovidOrganization covidOrganization=new CovidOrganization();
		covidOrganization.setReferralClinicID("2152").setReferralClinicName("Kane Medical");
		CovidPatient covidPatient=new CovidPatient(covidOrganization,"complete-data-1831");
		//covidPatient.setCovidCareId("complete-data-1831");
		CovidObservation covidObservation=new CovidObservation();
		covidObservation.setName("FEVER");
		covidObservation.setValue("FREQUENT");
		covidObservation.setEffectiveDateTime(new Date());
		List<CovidObservation> covidobservationLst=new ArrayList<CovidObservation>();
		covidobservationLst.add(covidObservation);
		covidPatient.setCovidObservationlst(covidobservationLst);
		//controller.createObservationsforExistingPatient(covidPatient);
		
	}
	

}
