package ch.fhnw.cssr.security;

import ch.fhnw.cssr.domain.User;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails extends ch.fhnw.cssr.domain.User implements UserDetails {

    private static final long serialVersionUID = 1L;
    private List<Integer> userRoles;

    public CustomUserDetails(User user, List<Integer> userRoles) {
        super(user);
        this.userRoles = userRoles;
    }

    /**
     * Gets the roles of a user.
     */
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roles = "";
        boolean first = true;
        for (int roleId : this.userRoles) {
            if (first) {
                first = false;
            } else {
                roles += ",";
            }
            roles += ch.fhnw.cssr.domain.UserRole.getRoleName(roleId);
        }
        return AuthorityUtils.commaSeparatedStringToAuthorityList(roles);
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
