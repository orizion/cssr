package ch.fhnw.cssr.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ch.fhnw.cssr.domain.UserRole;

@Repository
public interface UserRolesRepository extends CrudRepository<UserRole, Long> {

	public Iterable<UserRole> findByUserId(long userId);

}