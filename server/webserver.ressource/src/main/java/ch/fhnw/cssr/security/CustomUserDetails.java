package ch.fhnw.cssr.security;

import ch.fhnw.cssr.domain.User;
import edu.emory.mathcs.backport.java.util.Collections;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails extends ch.fhnw.cssr.domain.User implements UserDetails {

    private static final long serialVersionUID = 1L;

    public CustomUserDetails(User user) {
        super(user);
    }

    public static CustomUserDetails createFhnw(String email) {
        return new CustomUserDetails(new User(0, email, email));
    }
    
    /**
     * Gets the roles of a user.
     */
    @SuppressWarnings("unchecked")
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Integer roleId = super.getRoleId();
        if (roleId == null) {
            return (Collection<? extends GrantedAuthority>)Collections.emptyList();
        }
        String roleName = ch.fhnw.cssr.domain.Role.getDefaultRoleName(roleId.intValue());
        return AuthorityUtils.commaSeparatedStringToAuthorityList(roleName);
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }

    public String getUsername() {
        return super.getEmail();
    }

    public String getPassword() {
        return super.getPasswordEnc();
    }

}
