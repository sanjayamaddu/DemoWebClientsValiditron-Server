package au.unimelb.fht.repo;


import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import au.unimelb.fht.model.FHTPatient;

@Repository
public interface PatientRepository extends CrudRepository<FHTPatient, Integer> {
	
	boolean existsByFhirID(String fhirID);
	
	List<FHTPatient> findByclinicID(String clinicalID);
	
}
