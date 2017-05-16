package ch.fhnw.cssr.webserver.utils;

public class ResetPasswordParameters {

    private String oldPassword;
    private String newPassword;
    private boolean oldPasswordTempToken;
    
    public boolean isOldPasswordTempToken() {
        return oldPasswordTempToken;
    }

    public void setOldPasswordTempToken(boolean oldPasswordTempToken) {
        this.oldPasswordTempToken = oldPasswordTempToken;
    }

    public String getOldPassword() {
        return oldPassword;
    }
    
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
    
    public String getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
