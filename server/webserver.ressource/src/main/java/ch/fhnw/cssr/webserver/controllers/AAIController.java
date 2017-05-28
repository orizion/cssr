package ch.fhnw.cssr.webserver.controllers;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

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
public class AAIController {

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
     * @param creds
     *            The credentials
     * @return The tokenResult
     */
    @RequestMapping
    public ResponseEntity<String> loginAAI(HttpServletRequest request) {
        if (request.getMethod().toUpperCase().equals("OPTIONS")) {
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        String email = request.getHeader("mail");
        String surname = request.getHeader("surname");
        String givenname = request.getHeader("givennamae");
        if (email != null && surname != null && givenname != null) {
            userUtils.assureCreated(email);
            User us = userRepo.findByEmail(email);
            us.setDisplayName(surname + " " + givenname);
            userRepo.save(us);
            UserDetails userD = userDetailsService.loadUserByUsername(email);
            String t = TokenAuthenticationService.getJwtTokenResult(userD.getAuthorities(), email).getToken();
            return new ResponseEntity<String>(t, HttpStatus.OK);
        }

        Enumeration<String> strs = request.getHeaderNames();
        String output = "";
        while (strs.hasMoreElements()) {
            String h = strs.nextElement();
            output += "\r\n" + h + ":    " + request.getHeader(h);
        }
        return new ResponseEntity<String>(output, HttpStatus.OK);
    }
}
