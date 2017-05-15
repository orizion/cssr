package ch.fhnw.cssr.domain;

public class UserAddMeta {

    private String email;
    private String displayName;

    public UserAddMeta() {

    }

    public UserAddMeta(String email, String displayName) {
        super();
        this.email = email;
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}
