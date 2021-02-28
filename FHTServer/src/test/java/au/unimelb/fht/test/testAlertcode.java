package au.unimelb.fht.test;

public class testAlertcode {

	public static void main(String[] args) {
		System.out.println(isAlertBasedOnMental("mefntalNegative"));

	}
	
	private static boolean isAlertBasedOnMental(String alertcode) {
		return alertcode.contains("mental");
	}

}
