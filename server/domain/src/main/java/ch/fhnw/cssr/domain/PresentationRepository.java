package ch.fhnw.cssr.domain;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PresentationRepository extends CrudRepository<Presentation, Integer> {

	@Query("SELECT c FROM Presentation AS c")
	public List<Presentation> getAllFuture();
}