package au.unimelb.covidcare.test;

import org.hl7.fhir.r4.model.DetectedIssue.DetectedIssueSeverity;

import au.unimelb.covidcare.util.EnumFieldOrCheckInPROM;

public class EnumTest {

	public static void main(String[] args) {
		
		System.out.println(DetectedIssueSeverity.valueOf("LOW"));
	
		EnumFieldOrCheckInPROM covidCareFieldOrCheckInPROM = EnumFieldOrCheckInPROM.valueOf("HEART_RATE");
		System.out.println(covidCareFieldOrCheckInPROM.getName());
		
		for(EnumFieldOrCheckInPROM m: EnumFieldOrCheckInPROM.values()) {
			if(m.getName().equals("Pregnancy status")) {
			System.out.println(m);
			}
		}
		
		//System.out.println(EnumFieldOrCheckInPROM.values().equals("Pregnancy status"));
		
		
	}

}
