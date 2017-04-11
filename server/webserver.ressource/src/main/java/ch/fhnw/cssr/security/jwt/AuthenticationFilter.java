package ch.fhnw.cssr.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.filter.GenericFilterBean;

import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticationFilter extends GenericFilterBean {

    @Value("${cssr.jwt.algorithm}")
    private String algorithm;
    
    @Value("${cssr.jwt.secret}")
    private String secret;
    
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
                .getAuthentication((HttpServletRequest) request);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        HttpServletResponse res = (HttpServletResponse) response;
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        res.setHeader("Access-Control-Max-Age", "3600");
        res.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        filterChain.doFilter(request, response);
    }
}