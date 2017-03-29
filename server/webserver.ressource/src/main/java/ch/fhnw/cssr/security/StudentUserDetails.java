package ch.fhnw.cssr.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import ch.fhnw.cssr.domain.User;
import ch.fhnw.cssr.domain.UserRole;

public class StudentUserDetails extends ch.fhnw.cssr.domain.User implements UserDetails {

    private static final long serialVersionUID = 1989867L;

    /**
     * Creates a new student.
     * 
     * @param id
     *            The id of the Student
     * @param email
     *            The email of the student
     */
    public StudentUserDetails(long id, String email) {
        super(id, email, email);
        if (email == null) {
            throw new NullPointerException("email");
        }
        // TODO: Get UserName from AD
    }

    /**
     * Gets the roles of the student. (one of which will be student)
     */
    public Collection<? extends GrantedAuthority> getAuthorities() {

        String roles = UserRole.getRoleName(UserRole.ROLEID_STUDENT);
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
