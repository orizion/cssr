package ch.fhnw.cssr.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ch.fhnw.cssr.domain.Presentation;

@Repository
public interface PresentationRepository extends CrudRepository<Presentation, Integer> {

	@Query("SELECT c FROM Presentation AS c WHERE c.dateTime > CURRENT_TIMESTAMP()")
	public List<Presentation> getAllFuture();
}