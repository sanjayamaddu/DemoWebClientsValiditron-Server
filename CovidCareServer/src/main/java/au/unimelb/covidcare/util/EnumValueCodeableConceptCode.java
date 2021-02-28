package au.unimelb.covidcare.util;
/**
 * 
 * COVIDCare Check-in PROM[Dry cough,Fever,Fatigue,Shortness of breath & Lack of smell/taste]
 * Refer "COVIDCare-FHT-Interface-Documentation" for more details.
 * @author smaddumarach
 *
 */
public enum EnumValueCodeableConceptCode {

	NONE("260353006"),
	RARE("27789000"),
	FREQUENT("70232002"),
	CONSTANT("255238004");

	
	private String name;
	 
	EnumValueCodeableConceptCode(String name) {
        this.name = name;
    }
 
    public String getName() {
        return name;
    }
}
