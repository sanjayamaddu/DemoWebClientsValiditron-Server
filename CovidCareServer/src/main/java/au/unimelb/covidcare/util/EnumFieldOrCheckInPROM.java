package au.unimelb.covidcare.util;
/**
 * COVIDCare Check-ins
 * Refer "COVIDCare-FHT-Interface-Documentation" for more details.
 * @author smaddumarach
 *
 */
public enum EnumFieldOrCheckInPROM {
	//PROMS
	HEART_RATE("Heart rate"),
	RESPIRATORY_RATE("Respiratory rate"),
	SINGLE_BREATH_COUNT("Apnea duration"),
	TEMPERATURE("Body temperature"),
	OXYGEN_SATURATION("Oxygen saturation in Arterial blood"),
	DRY_COUGH("Dry cough"),
	FEVER("Fever"),
	FATIGUE("Fatigue"),
	SHORTNESS_OF_BREATH("Dyspnea"),
	LACK_OF_SMELL_OR_TASTE("Loss of sense of smell"),
	APATHETIC("Indifference"),
	DEPRESSED("Depressed"),
	ANXIOUS("Anxiety"),
	WORRIED("Worried"),
	//fields
	HAS_LUNG_CONDITION("Disorder of lung"),
	HAS_CARDIOVASCULAR_CONDITION("Disorder of cardiovascular system"),
	IS_SMOKER("Smoker"),
	HAS_DIABETES("Diabetes mellitus"),
	IS_OVERWEIGHT("Overweight"),
	HAS_HYPERTENSION("Hypertensive disorder, systemic arterial"),
	IS_PREGNANT("Pregnant"),
	CONTACT_OPT_OUT("Opt out of being contacted"),
	SYMPTOM_START_DATE("Time since symptom started"),
	TEST_RESULT("COVID-19 nucleic acid assay"),
	TEST_DATE("Test date");
	
	private String name;
	 
	EnumFieldOrCheckInPROM(String name) {
        this.name = name;
    }
 
    public String getName() {
        return name;
    }

}
