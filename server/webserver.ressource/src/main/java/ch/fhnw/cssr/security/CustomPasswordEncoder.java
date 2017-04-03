package ch.fhnw.cssr.security;

import org.springframework.security.crypto.password.PasswordEncoder;

import ch.fhnw.cssr.domain.PasswordHandler;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class CustomPasswordEncoder implements PasswordEncoder {

    Argon2 argon2;

    // Parameters are recommended at
    // https://paragonie.com/blog/2016/02/how-safely-store-password-in-2016
    final int prmN = 65536;
    final int prmR = 2;
    final int prmP = 1;

    public CustomPasswordEncoder() {
        argon2 = Argon2Factory.create();
    }

    /**
     * Returns the hash of the password given.
     */
    public String encode(CharSequence arg0) {
        return argon2.hash(prmR, prmN, prmP, arg0.toString());

    }

    /**
     * Tests whether the given rawPassword is equal to the given encoded password.
     */
    public boolean matches(CharSequence rawPassword, String encodedPassword) {

        if (encodedPassword.startsWith(PasswordHandler.ADM_PLACEHOLDER_PREFIX)) {
            return true; // TODO: AD Lookup
        }
        if (encodedPassword.startsWith(PasswordHandler.STUDENT_PLACEHOLDER_PREFIX)) {
            return true; // TODO: AD Lookup
        }

        String hash = argon2.hash(prmR, prmN, prmP, rawPassword.toString());

        // Verify password
        return argon2.verify(hash, encodedPassword);
    }

}
