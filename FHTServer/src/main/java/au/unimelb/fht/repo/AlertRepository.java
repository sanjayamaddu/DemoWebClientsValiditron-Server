package au.unimelb.fht.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import au.unimelb.fht.model.AlertType;
import au.unimelb.fht.model.FHTAlert;

public interface AlertRepository extends CrudRepository<FHTAlert, Integer>{
	public List<FHTAlert> findAllByAlertType(AlertType alertType);
	
	@Query("SELECT DISTINCT fhtalert.alertCode FROM FHTAlert fhtalert WHERE fhtalert.alertType=:alertType")
	public  List<String> findDistinctAlertCodeByAlertType(@Param("alertType")AlertType alertType);
	
}
