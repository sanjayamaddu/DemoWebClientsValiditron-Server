package au.unimelb.fht.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import au.unimelb.fht.Application;
import au.unimelb.fht.controller.FHTController;
import au.unimelb.fht.model.SearchParameter;

@SpringBootTest(classes = Application.class)
public class SimpleTestFHT extends AbstractTestNGSpringContextTests {
	
	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;
	
	@BeforeClass
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Autowired
	FHTController controller;
	
	
	@BeforeMethod
	public void setUp() {
		controller = new FHTController();
		}	

	@AfterMethod
	public void tearDown() {
		controller = null;
	}
	
	
	@Test
	public void testgetalerttypesbynamementalhealth() throws Exception {
		mockMvc.perform(get("/api/getAlertsByTypeName/{typeName}","Mental Health")).andExpect(status().isOk());
	}
	@Test
	public void testgetalerttypesbynamevitalsigns() throws Exception {
		mockMvc.perform(get("/api/getAlertsByTypeName/{typeName}","Vital Signs")).andExpect(status().isOk());
	}
	
	@Test
	public void testgetdistinctalerttypesbynamevitalsigns() throws Exception {
		mockMvc.perform(get("/api/getDistinctAlertsByTypeName/{typeName}","Vital Signs")).andExpect(status().isOk());
	}
	
	@Test
	public void getalertsbysearchcriteria() throws Exception {
		String sDate1="10/03/2021";
		Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
		mockMvc.perform(post("/api/getalertsbysearchcriteria/")
				.content(asJsonString(new SearchParameter("2152",null,null,null,null,null,"On",date1)))
			    .contentType(MediaType.APPLICATION_JSON)
			    .accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}
	
		
	@Test
	public void testfetchnewalerts() throws Exception {
	MvcResult mvcResult=mockMvc.perform(get("/api/fetchnewalerts/{practitionerID}","2152")).andExpect(status().isOk()).andReturn();
	System.out.println("Result:"+mvcResult.getResponse().getContentAsString());
	}
	
	@Test
//	public void testgetalertsbysearchcriteria() throws Exception {
//		mockMvc.perform(get("/api/testgetalertsbysearchcriteria/")
//				.content(asJsonString(new SearchParameter(null,null,null,null,null,null,"Before",null)))
//			    .contentType(MediaType.APPLICATION_JSON)
//			    .accept(MediaType.APPLICATION_JSON))
//		.andExpect(status().isOk());
//	}
	/**
	 * convert to json string.
	 * @param obj
	 * @return
	 */
	public static String asJsonString(final Object obj) {
	    try {
	        return new ObjectMapper().writeValueAsString(obj);
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}

}
