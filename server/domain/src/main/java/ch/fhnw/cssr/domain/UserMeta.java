package ch.fhnw.cssr.domain;

public class UserMeta {
    private final String email;
    private final String[] authorities;
    private final boolean isExtern;
    private final Long userId;

    /**
     * Creates a new user meta class.
     * @param isExtern The is Extern Flag
     * @param email The email
     * @param authorities The roles (should be one only, currently)
     * @param userId The id of the user.
     */
    public UserMeta(boolean isExtern, String email, String[] authorities, Long userId) {
        this.isExtern = isExtern;
        this.email = email;
        this.authorities = authorities;
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public Long getUserId() {
        return userId;
    }

    public String[] getAuthorities() {
        return authorities;
    }

    public boolean isExtern() {
        return isExtern;
    }

}
