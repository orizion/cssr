package ch.fhnw.cssr.domain.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ch.fhnw.cssr.domain.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
	public User findByEmail(String email);
	
	public User findByTempToken(String tempToken);

	@Query("SELECT c FROM User AS c WHERE c.displayName LIKE CONCAT('%', :searchString, '%') OR c.email LIKE CONCAT('%', :searchString, '%')")
    public Iterable<User> findByEmailOrDisplayName(@Param("searchString") String searchString);

}