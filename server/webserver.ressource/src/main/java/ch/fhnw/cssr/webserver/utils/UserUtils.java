package ch.fhnw.cssr.webserver.utils;

import java.security.Principal;
import java.util.Arrays;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import ch.fhnw.cssr.domain.Role;
import ch.fhnw.cssr.domain.User;
import ch.fhnw.cssr.domain.repository.UserRepository;
import ch.fhnw.cssr.security.CustomUserDetails;

public class UserUtils {

    @Autowired
    private UserRepository repo;
    
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Checks whether a given user has a certain spring related role.
     * 
     * @param user
     *            The user.
     * @param roleIds
     *            The roleIds.
     * @return A boolean indicating if the user has one of the roles.
     */
    public boolean hasDefaultRole(Principal user, Integer... roleIds) {
        UserDetails dt = userDetailsService.loadUserByUsername(user.getName());

        Stream<String> roleNames = Arrays.stream(roleIds)
                .map(roleId -> Role.getDefaultRoleName(roleId));

        return dt.getAuthorities().stream().map(a -> a.getAuthority())
                .anyMatch(p -> roleNames.anyMatch(r -> r.equals(p)));
    }
    
    /**
     * Creates a FHNW user if it does not exist.
     * 
     * @param email
     *            The email address
     */
    public boolean assureCreated(String email) {
        if (!User.isFhnwEmail(email)) {
            throw new IllegalArgumentException("Must be an fhnw email");
        }
        UserDetails dt = userDetailsService.loadUserByUsername(email);
        if (dt instanceof CustomUserDetails) {
            User us = ((CustomUserDetails) dt).copy();
            if (us.getUserId() == null || us.getUserId() == Long.valueOf(0)) {
                repo.save(us); // Save user persistently if not already done
                return true;
            }
        }
        return false;
    }
}
