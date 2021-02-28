
package au.unimelb.fht.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import au.unimelb.fht.model.FHTPatient;

public interface FHTPatientCustomRepository extends JpaRepository<FHTPatient, Integer> {
	
	List<FHTPatient> findFHTPatientByClinicIDAndName(String clinicID,String name);
	
	List<FHTPatient> findFHTPatientByClinicIDAndEmail(String clinicID,String email);
	
	List<FHTPatient> findFHTPatientByClinicIDAndPhone(String clinicID,String phone);
	
	List<FHTPatient> findFHTPatientByClinicIDAndNameAndEmail(String clinicID,String name,String email);
	
	List<FHTPatient> findFHTPatientByClinicIDAndNameAndPhone(String clinicID,String name,String phone);
	
	List<FHTPatient> findFHTPatientByClinicIDAndEmailAndPhone(String clinicID,String email,String phone);

	List<FHTPatient> findFHTPatientByClinicIDAndNameAndEmailAndPhone(String clinicID,String name,String email,String phone);
}
