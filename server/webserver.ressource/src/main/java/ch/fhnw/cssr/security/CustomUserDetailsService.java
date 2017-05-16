package ch.fhnw.cssr.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ch.fhnw.cssr.domain.User;
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

}
