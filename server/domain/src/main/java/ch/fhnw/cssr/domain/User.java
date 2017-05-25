package ch.fhnw.cssr.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User implements Serializable {

    private static final long serialVersionUID = 10023987L;

    public static final String FHNW_DOMAIN = "fhnw.ch";
    public static final String ACTIVE_DIRECTORY_LOOKUP_PREFIX = "ACTIVE_DIRECTORY_LOOKUP_HACK_PREFIX:::::";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "userId")
    private Long userId;

    @Column(name = "displayName")
    private String displayName;

    @Column(name = "passwordEnc")
    private String passwordEnc;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "tempToken")
    private String tempToken;

    @Column(name = "tempTokenExpiresAt")
    private LocalDateTime tempTokenExpiresAt;

    @Column(name = "userRoleId")
    private Integer userRoleId;

    public User() {

    }

    /**
     * This is a constructor for students or other AD Users only
     */
    public User(long userId, String email, String userName) {
        this.email = email;
        if (isFhnwEmail(email)) {
            this.passwordEnc = ACTIVE_DIRECTORY_LOOKUP_PREFIX + email;
        } else {
            throw new IllegalArgumentException("email");
        }
    }

    public User(String email, String displayName, String passwordEnc, String tempToken,
            LocalDateTime tempTokenExpiresAt) {
        this.email = email;
        this.displayName = displayName;
        this.passwordEnc = passwordEnc;
        this.tempToken = tempToken;
        this.tempTokenExpiresAt = tempTokenExpiresAt;
    }

    protected User(User copyUser) {
        if (copyUser == null)
            throw new NullPointerException("copyUser");
        this.userId = new Long(0).equals(copyUser.userId) ? null : copyUser.userId;
        this.email = copyUser.email;
        this.passwordEnc = copyUser.passwordEnc;
        this.tempToken = copyUser.tempToken;
        this.tempTokenExpiresAt = copyUser.tempTokenExpiresAt;
        this.displayName = copyUser.displayName;
        this.userRoleId = copyUser.userRoleId;
    }

    public User copy() {
        return new User(this);
    }

    public static boolean isFhnwEmail(String email) {
        return email.endsWith("@" + FHNW_DOMAIN) || email.endsWith("." + FHNW_DOMAIN); // A
                                                                                       // subdomain
    }

    public boolean isExtern() {
        return !isFhnwEmail(email);
    }

    public Long getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Integer getRoleId() {
        return this.userRoleId;
    }

    public void setRoleId(Integer roleId) {
        this.userRoleId = roleId;
    }

    public String getPasswordEnc() {
        if (isFhnwEmail(email)) {
            return ACTIVE_DIRECTORY_LOOKUP_PREFIX + email;
        }
        return passwordEnc;
    }

    public void setPasswordEnc(String passwordEnc) {
        this.passwordEnc = passwordEnc;
    }

    public String getEmail() {
        return email;
    }

    public String getTempToken() {
        return tempToken;
    }

    public void setTempToken(String tempToken, LocalDateTime expiresAt) {

        this.tempToken = tempToken;
        this.tempTokenExpiresAt = expiresAt;
    }

    public LocalDateTime getTempTokenExpiresAt() {
        return tempTokenExpiresAt;
    }

}