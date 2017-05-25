package ch.fhnw.cssr.security.jwt;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.fhnw.cssr.domain.User;
import ch.fhnw.cssr.domain.repository.UserRepository;
import ch.fhnw.cssr.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;

public class TokenAuthenticationService {
    static final long EXPIRATIONTIME = 3_600_000; // 60 Minutes
    static final String TOKEN_PREFIX = "Bearer";
    static final String HEADER_STRING = "Authorization";
    static final String ROLES_KEY = "r";

    static String JwtHeader = "eyJhbGciOiJIUzUxMiJ9";

    static SignatureAlgorithm Algorithm = SignatureAlgorithm.HS512;
    static String Secret = "ThisIsASecret";

    /**
     * Initialize this class with some meaningful parameters.
     * 
     * @param algo
     *            The Signature algorithm, such as HS412
     * @param secret
     *            The secret key
     */
    public static void initialize(SignatureAlgorithm algo, String secret) {
        TokenAuthenticationService.Algorithm = algo;
        TokenAuthenticationService.Secret = secret;

        JwtBuilder builder = Jwts.builder();
        Claims s = new DefaultClaims();
        s.setSubject("invalidsubject");
        builder.setClaims(s);
        String jwtToken = builder.signWith(Algorithm, Secret).compact();
        TokenAuthenticationService.JwtHeader = jwtToken.substring(0, jwtToken.indexOf("."));
    }

    /**
     * Gets the JWT Token for the given user.
     * 
     * @param authorities
     *            The roles for the user
     * @param username
     *            The username
     * @return The jwt token result
     */
    public static TokenResult getJwtTokenResult(Collection<? extends GrantedAuthority> authorities,
            String username) {
        Claims s = new DefaultClaims();
        String rolesStr = String.join(",", AuthorityUtils.authorityListToSet(authorities));
        s.put(ROLES_KEY, rolesStr);
        s.setSubject(username);
        s.setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME));

        JwtBuilder builder = Jwts.builder();
        builder.setClaims(s);
        String jwtToken = builder.signWith(Algorithm, Secret).compact();

        // Remove headers, as this is often a cause for weak security
        jwtToken = jwtToken.substring(jwtToken.indexOf('.') + 1);
        return new TokenResult(jwtToken);
    }

    static void addAuthentication(HttpServletResponse res,
            Collection<? extends GrantedAuthority> authorities, String username)
            throws IOException {

        res.setContentType("application/json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(res.getWriter(), getJwtTokenResult(authorities, username));
    }
    
    private static Authentication getTempTokenAuthentication(String tempToken, 
            UserRepository userRepository) {
        User us = userRepository.findByTempToken(tempToken);
        if (us == null) {
            return null;
        }
        if (us.getTempTokenExpiresAt().compareTo(LocalDateTime.now()) < 0) {
            return null;
        }
        CustomUserDetails dt = new CustomUserDetails(us);
        return new UsernamePasswordAuthenticationToken(dt.getUsername(), null,
                dt.getAuthorities()); 
    }
    
    static Authentication getAuthentication(HttpServletRequest request,
            UserRepository userRepository) {
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {
            // parse the token.
            Jws<Claims> claims = Jwts.parser().setSigningKey(Secret)
                    .parseClaimsJws(JwtHeader + "." + token.replace(TOKEN_PREFIX, "").trim());
            if (claims.getHeader().getAlgorithm().equals("none")) {
                throw new IllegalArgumentException("Algorithm not valid");
            }
            Claims jwtBody = claims.getBody();
            String roleList = (String) jwtBody.get(ROLES_KEY);
            List<GrantedAuthority> roles = AuthorityUtils
                    .commaSeparatedStringToAuthorityList(roleList);

            String user = jwtBody.getSubject();

            return user != null ? new UsernamePasswordAuthenticationToken(user, null, roles) : null;
        }
        String tempToken = request.getMethod().equals("GET") ? request.getParameter("tempToken")
                : null;
        if (tempToken != null) {
            return getTempTokenAuthentication(tempToken, userRepository);
        }
        if (request.getCookies() != null && request.getCookies().length > 0) {
            // TODO: We should validate the cookies here and accept a cookie from the AAI Proxy
        }
        return null;
    }
}