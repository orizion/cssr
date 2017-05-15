package ch.fhnw.cssr.security;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ch.fhnw.cssr.domain.User;
import ch.fhnw.cssr.domain.Role;
import ch.fhnw.cssr.domain.repository.UserRepository;

@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Gets the user from database and checks if the user already exists. If the user does not
     * exist, one will be created.
     */
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (null == user) {
            if (User.isFhnwEmail(email)) {
                UserDetails dt = CustomUserDetails.createFhnw(email);
                return dt;
            }
            throw new UsernameNotFoundException("No user present with email: " + email);
        } else {
            return new CustomUserDetails(user);
        }
    }

    /**
     * Creates a FHNW user if it does not exist.
     * 
     * @param email
     *            The email address
     * @param repo
     *            The user repository.
     */
    public static boolean assureCreated(UserDetailsService service, UserRepository repo,
            String email) {
        if (!User.isFhnwEmail(email)) {
            throw new IllegalArgumentException("Must be an fhnw email");
        }
        UserDetails dt = service.loadUserByUsername(email);
        if (dt instanceof CustomUserDetails) {
            User us = ((CustomUserDetails) dt).copy();
            if (us.getUserId() == 0 || us.getUserId() == null) {
                repo.save(us); // Save user persistently if not already done
                return true;
            }
        }
        return false;
    }
}
