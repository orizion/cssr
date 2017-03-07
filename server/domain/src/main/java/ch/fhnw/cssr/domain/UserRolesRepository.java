package ch.fhnw.cssr.domain;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRolesRepository extends CrudRepository<UserRole, Long> {

//	@Query("select a.roleId from UserRole a inner join User b on b.userId=a.userId where b.email=?1")
//	public List<Integer> findRoleByEmail(String email);
	
	public Iterable<UserRole> findByUserId(long userId);

}