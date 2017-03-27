package ch.fhnw.cssr.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends CrudRepository<Email, Long> {

	
}