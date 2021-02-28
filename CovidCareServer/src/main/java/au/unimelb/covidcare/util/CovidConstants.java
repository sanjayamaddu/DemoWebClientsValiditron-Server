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
