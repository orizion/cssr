package ch.fhnw.cssr.domain.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ch.fhnw.cssr.domain.Email;

@Repository
public interface EmailRepository extends CrudRepository<Email, Long> {

	@Query("SELECT em FROM Email AS em WHERE em.sentDate IS NULL AND em.tryCount < 4")
	public Iterable<Email> findNotSent();
}