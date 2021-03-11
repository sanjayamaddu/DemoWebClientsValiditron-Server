package au.unimelb.fht.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import au.unimelb.fht.model.FHTEncounter;

@Repository
public interface EncounterRepository extends CrudRepository<FHTEncounter, Integer> {
	
	boolean existsByFhirID(String fhirID);
	
	List<FHTEncounter>  findByCheckinDateTimeBeforeOrderByCheckinDateTimeDesc(Date searchDate);
	
	List<FHTEncounter>  findByCheckinDateTimeAfterOrderByCheckinDateTimeDesc(Date searchDate);
	
	List<FHTEncounter>  findByCheckinDateTimeBetweenOrderByCheckinDateTimeDesc(Date start,Date end);
	
	//@Query(value="select * from encounter e left join patient p on e.patient =p.id where p.clinicID=:clinicID ",nativeQuery = true)
//	@Query(value="FROM FHTEncounter AS e LEFT JOIN e.fhtpatient As p LEFT JOIN e.fhtAlerts As a  "
//			+ "WHERE p.clinicID=IFNULL(:clinicID,p.clinicID) AND p.name=IFNULL(:name,p.name) "
//			+ "AND p.email=IFNULL(:email,p.email) AND p.phone=IFNULL(:phone,p.phone) "
//			+ "AND e.checkinDateTime :operand IFNULL(:checkinDateTime,e.checkinDateTime)"
//			+ "AND a in(FROM FHTAlert as fa  WHERE (fa.alertType.name='Vital Signs' AND fa.alertCode=IFNULL(:vitalalertcode,fa.alertCode)"
//			+ " ) OR(fa.alertType.name='Mental Health' AND fa.alertCode=IFNULL(:mentalalertcode,fa.alertCode)))")
//	List<FHTEncounter> findEncountersWithSearchCriteria(@Param("clinicID") String clinicID,@Param("name") String name,
//			@Param("email") String email,@Param("phone") String phone,@Param("checkinDateTime") Date checkinDateTime,
//			@Param("vitalalertcode") String vitalalertcode,@Param("mentalalertcode") String mentalalertcode,@Param("operand") String operand);
}

