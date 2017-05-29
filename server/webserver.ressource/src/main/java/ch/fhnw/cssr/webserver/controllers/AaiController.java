package ch.fhnw.cssr.webserver.controllers;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.fhnw.cssr.domain.Email;
import ch.fhnw.cssr.domain.EmailView;
import ch.fhnw.cssr.domain.User;
import ch.fhnw.cssr.domain.UserMeta;
import ch.fhnw.cssr.domain.repository.EmailRepository;
import ch.fhnw.cssr.domain.repository.UserRepository;
import ch.fhnw.cssr.mailutils.EmailTemplate;
import ch.fhnw.cssr.security.jwt.AccountCredentials;
import ch.fhnw.cssr.security.jwt.TokenAuthenticationService;
import ch.fhnw.cssr.security.jwt.TokenResult;
import ch.fhnw.cssr.webserver.utils.ResetPasswordParameters;
import ch.fhnw.cssr.webserver.utils.UserUtils;

@RestController
@RequestMapping("/aai")
public class AaiController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private UserRepository userRepo;

    /**
     * Use this method to login the user.
     * 
     * @param request
     *            The request
     * @param response
     *            The response
     * @throws IOException The exception if the URL is invalid (should not happen). 
     */
    @RequestMapping
    public void loginAai(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String email = request.getHeader("mail");
        String surname = request.getHeader("surname");
        String givenname = request.getHeader("givenName");
        String forwardedHost = request.getHeader("X-Forwarded-Host");
        if (!forwardedHost.split(",")[0].equals("www.cs.technik.fhnw.ch")) {
            response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
            return;
        }
        
        if (email != null && surname != null && givenname != null) {
            userUtils.assureCreated(email);
            User us = userRepo.findByEmail(email);
            us.setDisplayName(surname + " " + givenname);
            userRepo.save(us);
            UserDetails userD = userDetailsService.loadUserByUsername(email);
            String t = TokenAuthenticationService.getJwtTokenResult(userD.getAuthorities(), email)
                    .getToken();
            response.sendRedirect("https://www.cs.technik.fhnw.ch/wodss17-6/index.html#token=" + t);
        } else {
            response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
            return;
        }
    }
}
