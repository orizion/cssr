package ch.fhnw.cssr.security;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ch.fhnw.cssr.domain.User;
import ch.fhnw.cssr.domain.UserRepository;
import ch.fhnw.cssr.domain.UserRolesRepository;

@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;
	private final UserRolesRepository userRolesRepository;

	@Autowired
	public CustomUserDetailsService(UserRepository userRepository, UserRolesRepository userRolesRepository) {
		this.userRepository = userRepository;
		this.userRolesRepository = userRolesRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email);
		if (null == user) {
			if(email.toLowerCase().endsWith("@students.fhnw.ch")){
				// TODO: Implement AD Lookup (Domain EDU)
			}
			if(email.toLowerCase().endsWith("@fhnw.ch")){
				// TODO: Implement AD Lookup (Domain ADM)
			}
			throw new UsernameNotFoundException("No user present with email: " + email);
		} else {
			List<Integer> userRoles = userRolesRepository.findRoleByEmail(email);
			return new CustomUserDetails(user, userRoles);
		}
	}
}