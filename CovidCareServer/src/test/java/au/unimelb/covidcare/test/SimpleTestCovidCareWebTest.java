package au.unimelb.covidcare.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import au.unimelb.covidcare.Application;
import au.unimelb.covidcare.controller.CovidCareController;


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
	

	@Test
	public void testgetPatients() throws Exception {
		mockMvc.perform(get("/api/getpatients")).andExpect(status().isOk());
	}
	
	@Test
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
	
	

}
