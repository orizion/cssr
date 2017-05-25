package ch.fhnw.cssr.domain;

public class UserMeta {
    private final String email;
    private final String[] authorities;
    private final boolean isExtern;
    private final Long userId;

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
