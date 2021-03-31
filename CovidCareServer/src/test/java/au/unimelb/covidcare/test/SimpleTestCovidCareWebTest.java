package au.unimelb.covidcare.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import au.unimelb.covidcare.Application;
import au.unimelb.covidcare.controller.CovidCareController;
import au.unimelb.covidcare.model.CovidDetectedIssue;
import au.unimelb.covidcare.model.CovidEncounter;
import au.unimelb.covidcare.model.CovidObservation;
import au.unimelb.covidcare.model.CovidOrganization;
import au.unimelb.covidcare.model.CovidPatient;


@SpringBootTest(classes = Application.class)
public class SimpleTestCovidCareWebTest extends AbstractTestNGSpringContextTests {
	
	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;
	
	@BeforeClass
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

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
	public void testgetReferral() throws Exception {
		mockMvc.perform(get("/api/getreferrals")).andExpect(status().isOk());
	}
	
	//@Test
	public void testgetPatientByRefferalandCovidcareID() throws Exception {
		mockMvc.perform(get("/api/getpatientbyreferralandcovidcareid/{referralClinicId}/{covidcareid}","1858","PAT00004")).andExpect(status().isOk());
	}
	
	

	@Test
	public void testgetPatients() throws Exception {
		mockMvc.perform(get("/api/getpatients")).andExpect(status().isOk());
	}
	
	//@Test
	public void testgetAllCovidCardIDS() throws Exception {
		mockMvc.perform(get("/api/getcovidcareids")).andExpect(status().isOk());
	}
	
	@Test
	public void testgetEncounter() throws Exception {
		mockMvc.perform(get("/api/getencounterlistasdisplayrow")).andExpect(status().isOk());
	}
	
	
	
	@Test
	public void testgetEncounters() throws Exception {
		mockMvc.perform(get("/api/getencounterlistasdisplayrow")).andExpect(status().isOk());
	}
	@Test
	public void testgetnaxtavailablacovidcareid() throws Exception {
		mockMvc.perform(get("/api/getnaxtavailablacovidcareid")).andExpect(status().isOk());
	}
	
	@Test
	public void testgetObservation() throws Exception {
		mockMvc.perform(get("/api/getobservations")).andExpect(status().isOk());
	}
	
	@Test
	public void testgetFhirLog() throws Exception {
		mockMvc.perform(get("/api/getfhirlog")).andExpect(status().isOk());
	}
	
	@Test
	public void testgetSessionData() throws Exception {
		mockMvc.perform(get("/api/viewSessionData")).andExpect(status().isOk());
	}
	
	@Test
	public void testgetRequestSeession() throws Exception {
		mockMvc.perform(get("/api/getsessionfhirlog/{fhirmessage}","session_getparents")).andExpect(status().isOk());
	}
	
	@Test
    public void testgetPatient() throws Exception{
        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/api/getpatients")
                                        .session(session);
        this.mockMvc.perform(builder)
                    .andExpect(MockMvcResultMatchers.status()
                                                    .isOk());

    }
	
	@Test
	public void testCreatePatient() throws Exception {
		CovidOrganization covidOrganization=new CovidOrganization();
		covidOrganization.setReferralClinicID("2152").setReferralClinicName("Kane Medical");
		CovidPatient covidPatient=new CovidPatient(covidOrganization,"PAT01006");
		//covidPatient.setCovidCareId("complete-data");
		covidPatient.setName("printing")
		.setGender("MALE")
		.setAge(49)
		.setMobile("0401523654")
		.setEmail("testmyemail@testing.con.ad.com")
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
		mockMvc.perform(post("/api/postpatient")
				.content(asJsonString(covidPatient))
			    .contentType(MediaType.APPLICATION_JSON)
			    .accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}

	@Test
	public void testCreateEncounter() throws Exception {
		CovidEncounter encounter=new CovidEncounter();
		encounter.setCovidcareID("PAT01005");
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
		mockMvc.perform(post("/api/postencounterobservationsanddetectedissues")
				.content(asJsonString(encounter))
			    .contentType(MediaType.APPLICATION_JSON)
			    .accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
		
		
	}
	
	
	/**
	 * convert to json string.
	 * @param obj
	 * @return
	 */
	private static String asJsonString(final Object obj) {
	    try {
	        return new ObjectMapper().writeValueAsString(obj);
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}
	
	

}
