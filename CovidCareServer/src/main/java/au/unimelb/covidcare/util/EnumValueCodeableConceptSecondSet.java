package au.unimelb.covidcare.util;

/**
 * COVIDCare Check-in PROM[Apathetic,Depressed,Anxious & Worried]
 * Refer "COVIDCare-FHT-Interface-Documentation" for more details.
 * @author smaddumarach
 *
 */
public enum EnumValueCodeableConceptSecondSet {
	
	NOTATALL("Not at all"),
	SEVERALDAYS("Several days"),
	MORETHANHALFDAYS("More than half the days"),
	NEARLYEVERYDAYS("Nearly every day");

	private String display;
	 
	EnumValueCodeableConceptSecondSet(String display) {
        this.display = display;
    }

	public String getDisplay() {
		return display;
	}
}
