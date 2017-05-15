package ch.fhnw.cssr.domain.repository;

import org.springframework.stereotype.Repository;
import ch.fhnw.cssr.domain.Role;

@Repository
public interface RoleRepository extends org.springframework.data.repository.Repository<Role, Integer> {
   
    /**
     * Retrieves an entity by its id.
     * 
     * @param id must not be {@literal null}.
     * @return the entity with the given id or {@literal null} if none found
     * @throws IllegalArgumentException if {@code id} is {@literal null}
     */
    Role findOne(Integer id);

    /**
     * Returns whether an entity with the given id exists.
     * 
     * @param id must not be {@literal null}.
     * @return true if an entity with the given id exists, {@literal false} otherwise
     * @throws IllegalArgumentException if {@code id} is {@literal null}
     */
    boolean exists(Integer id);

    /**
     * Returns all instances of the type.
     * 
     * @return all entities
     */
    Iterable<Role> findAll();
}
