
package au.unimelb.fht.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import au.unimelb.fht.model.FHTPatient;

public interface FHTPatientCustomRepository extends JpaRepository<FHTPatient, Integer> {
	
	List<FHTPatient> findFHTPatientByClinicIDAndNameContains(String clinicID,String name);
	
	List<FHTPatient> findFHTPatientByClinicIDAndEmailContains(String clinicID,String email);
	
	List<FHTPatient> findFHTPatientByClinicIDAndPhoneContains(String clinicID,String phone);
	
	List<FHTPatient> findFHTPatientByClinicIDAndNameContainsAndEmailContains(String clinicID,String name,String email);
	
	List<FHTPatient> findFHTPatientByClinicIDAndNameContainsAndPhoneContains(String clinicID,String name,String phone);
	
	List<FHTPatient> findFHTPatientByClinicIDAndEmailContainsAndPhoneContains(String clinicID,String email,String phone);

	List<FHTPatient> findFHTPatientByClinicIDAndNameContainsAndEmailContainsAndPhoneContains(String clinicID,String name,String email,String phone);
}
