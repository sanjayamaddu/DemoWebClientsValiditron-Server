package au.unimelb.covidcare.util;

/**
 * COVIDCare Check-ins
 * Encounter details are transferred with following details.
 * @author smaddumarach
 *
 */
public class CovidConstants {
	
	public static final String DASHBOARD="Dashboard";
	
	public static final String PUSH_NOTIFICATION="Push notification";
	
	public static final String CHECKIN_REASONCODE_CODE_DASHBOARD="270427003";
	
	public static final String CHECKIN_REASONCODE_CODE_PUSH="373113001";
	
	public static final String CHECKIN_REASONCODE_TEXT_DASHBOARD="Instigated Check-in via dashboard";
	
	public static final String CHECKIN_REASONCODE_TEXT_PUSH="Instigated Check-In via push notification";
	
	public static final String PREFIX_MENTAL = "mental";
	
	//	Fetches in the service
	
	public static final String SESSION_GETPARENTS="session_getparents";
	
	public static final String SESSION_GETENCOUNTERS="session_getencounters";
	
	public static final String SESSION_GETPARENT_COVID_ID="session_getparent_covid_id";
	
	public static final String SESSION_GETENCOUNTER_FHIR_ID="session_getencounter_fhir_id";
	
	
	public static final String SESSION_CREATEPARENT="session_createparent";
	
	//public static final String SESSION_CREATEPARENT_RESPONSE="session_createparent_response";
	
	public static final String SESSION_CREATE_CHECKIN="session_createcheckin";
	
	//public static final String SESSION_CREATE_CHECKIN_RESPONSE="session_createcheckin_response";
	
	
	public static String getCode(String value) {
		if(value.equals(DASHBOARD)) {
			return 	CHECKIN_REASONCODE_CODE_DASHBOARD;
		}else if(value.equals(PUSH_NOTIFICATION)) {
			return CHECKIN_REASONCODE_CODE_PUSH;
		}else {
			return null;
		}
	}
	
	public static String getCodeText(String value) {
		if(value.equals(DASHBOARD)) {
			return 	CHECKIN_REASONCODE_TEXT_DASHBOARD;
		}else if(value.equals(PUSH_NOTIFICATION)) {
			return CHECKIN_REASONCODE_TEXT_PUSH;
		}else {
			return null;
		}
	}
	
}
