package au.unimelb.fht.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import au.unimelb.fht.model.AlertType;

@Repository
public interface AlertTypeRepository extends CrudRepository<AlertType, Integer> {
	
	AlertType findByName(String name);
	boolean existsByName(String name);
}
