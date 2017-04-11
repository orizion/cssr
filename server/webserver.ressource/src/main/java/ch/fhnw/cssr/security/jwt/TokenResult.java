package ch.fhnw.cssr.security.jwt;

public class TokenResult {
    private String token;

    public TokenResult() {
    }

    public TokenResult(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
