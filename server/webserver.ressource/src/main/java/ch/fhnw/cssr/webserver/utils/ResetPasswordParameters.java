package ch.fhnw.cssr.webserver.utils;

public class ResetPasswordParameters {

    private String oldPassword;
    private String newPassword;
    private boolean isOldPasswordTempToken;
    
    public boolean isOldPasswordTempToken() {
        return isOldPasswordTempToken;
    }

    public void setIsOldPasswordTempToken(boolean isOldPasswordTempToken) {
        this.isOldPasswordTempToken = isOldPasswordTempToken;
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
