package ch.fhnw.cssr.domain;

public class UserAddMeta {

    private String email;
    private String displayName;

    public UserAddMeta() {

    }

    /** 
     * Creates the meta data.
     * @param email The email
     * @param displayName The name to be displayed
     */
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
