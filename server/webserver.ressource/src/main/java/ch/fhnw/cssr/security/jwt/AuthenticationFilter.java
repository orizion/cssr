package ch.fhnw.cssr.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.GenericFilterBean;

import ch.fhnw.cssr.domain.repository.UserRepository;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticationFilter extends GenericFilterBean {

    private volatile String algorithm;
    
    private volatile String secret;
    
    private final UserRepository userRepository; 
    
    /**
     * Creates a new filter.
     * @param userRepo Using the UserRepo for temp tokens
     * @param algorithm The specified algorithm
     * @param secret The given secret
     */
    public AuthenticationFilter(UserRepository userRepo, 
            String algorithm,
            String secret) {
        this.userRepository = userRepo;
        this.algorithm = algorithm;
        this.secret = secret;
    }
    
    @Override
    protected void initFilterBean() throws ServletException {
        super.initFilterBean();
        SignatureAlgorithm algo = SignatureAlgorithm.forName(algorithm);
        TokenAuthenticationService.initialize(algo, secret);
        
        // This class should not store these values
        this.secret = null;
        this.algorithm = null;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        Authentication authentication = TokenAuthenticationService
                .getAuthentication((HttpServletRequest) request, userRepository);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}