package ch.fhnw.cssr.security.jwt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.fhnw.cssr.domain.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticationFilter extends GenericFilterBean {

    private volatile String algorithm;

    private volatile String secret;

    private final UserRepository userRepository;

    private final ObjectMapper mapper;
    
    /**
     * Creates a new filter.
     * 
     * @param userRepo
     *            Using the UserRepo for temp tokens
     * @param algorithm
     *            The specified algorithm
     * @param secret
     *            The given secret
     * @param mapper
     *            The json mapper          
     */
    public AuthenticationFilter(UserRepository userRepo, String algorithm, String secret, 
            ObjectMapper mapper) {
        this.userRepository = userRepo;
        this.algorithm = algorithm;
        this.secret = secret;
        this.mapper = mapper;
    }

    @Override
    protected void initFilterBean() throws ServletException {
        super.initFilterBean();
        if (algorithm != null) {
            SignatureAlgorithm algo = SignatureAlgorithm.forName(algorithm);
            TokenAuthenticationService.initialize(algo, secret);
        }
        // This class should not store these values
        this.secret = null;
        this.algorithm = null;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            Authentication authentication = TokenAuthenticationService
                    .getAuthentication((HttpServletRequest) request, userRepository);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException ejw) {
            HttpServletResponse rh = ((HttpServletResponse)response);
            rh.setStatus(HttpStatus.UNAUTHORIZED_401);
            rh.setContentType(MediaType.APPLICATION_JSON.toString());
            Map<String, Object> errorMsg = new HashMap<String, Object>();
            errorMsg.put("error", "token expired");
            rh.getWriter().write(mapper.writeValueAsString(errorMsg));
        }
    }
}