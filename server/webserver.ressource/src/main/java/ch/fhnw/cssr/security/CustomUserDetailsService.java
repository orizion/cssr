package ch.fhnw.cssr.security;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ch.fhnw.cssr.domain.User;
import ch.fhnw.cssr.domain.UserRepository;
import ch.fhnw.cssr.domain.UserRole;
import ch.fhnw.cssr.domain.UserRolesRepository;

@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRolesRepository userRolesRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository,
            UserRolesRepository userRolesRepository) {
        this.userRepository = userRepository;
        this.userRolesRepository = userRolesRepository;
    }

    /**
     * Gets the user from database and checks if the user already exists. If the user does not
     * exist, one will be created.
     */
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (null == user) {
            if (email.toLowerCase().endsWith(User.StudentsEmailPostfix)) {
                // TODO: Implement AD Lookup (Domain EDU)
                // TODO: Find better location to save user, he should have entered the password
                // already
                StudentUserDetails dt = new StudentUserDetails(0, email);
                this.userRepository.save(dt.copy());
                return dt;
            }
            if (email.toLowerCase().endsWith(User.AdmEmailPostfix)) {
                // TODO: Implement AD Lookup (Domain ADM)

            }
            throw new UsernameNotFoundException("No user present with email: " + email);
        } else {
            if (email.toLowerCase().endsWith(User.StudentsEmailPostfix)) {
                return new StudentUserDetails(user.getUserId(), email);
            }

            Iterable<UserRole> roles = userRolesRepository.findByUserId(user.getUserId());
            List<Integer> userRoles = new ArrayList<Integer>();
            for (UserRole ur : roles) {
                userRoles.add(ur.getRoleId());
            }
            return new CustomUserDetails(user, userRoles);
        }
    }
}
