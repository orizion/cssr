package ch.fhnw.cssr.domain;

import java.io.Serializable;
import java.time.LocalTime;

import javax.persistence.*;

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
    private LocalTime tempTokenExpiresAt;

    public Long getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPasswordEnc() {
        if(isFhnwEmail(email)) {
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

    public void setTempToken(String tempToken, LocalTime expiresAt) {
        this.tempToken = tempToken;
        this.tempTokenExpiresAt = expiresAt;
    }

    public LocalTime getTempTokenExpiresAt() {
        return tempTokenExpiresAt;
    }

    public User() {

    }

    /**
     * This is a constructor for students or other AD Users only
     */
    protected User(long userId, String email, String userName) {
        this.email = email;
        if (isFhnwEmail(email)) {
            this.passwordEnc = ACTIVE_DIRECTORY_LOOKUP_PREFIX + email;
        } else {
            throw new IllegalArgumentException("email");
        }
    }

    public User(String email, String displayName, String passwordEnc, String tempToken,
            LocalTime tempTokenExpiresAt) {
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
    }

    public User copy() {
        return new User(this);
    }

    public static boolean isFhnwEmail(String email) {
        return !email.endsWith("@" + FHNW_DOMAIN)
            && !email.endsWith("." + FHNW_DOMAIN); // A subdomain
    }
    
    public boolean isExtern() {
        return !isFhnwEmail(email);
    }

}