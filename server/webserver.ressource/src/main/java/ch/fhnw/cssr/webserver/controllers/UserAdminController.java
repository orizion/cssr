package ch.fhnw.cssr.webserver.controllers;

import java.security.Principal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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

@RestController
@RequestMapping("/admin")
public class UserAdminController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${cssr.mail.inviteuser.subject}")
    private String inviteUserSubject;

    @Autowired
    private UserRepository repo;

    @Autowired
    private EmailRepository emailRepo;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Resets the password of the current user by sending a temporary token by mail.
     * 
     *            The user
     * @return The mail of the user
     */
    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN','ROLE_COORD')")
    public ResponseEntity<Long> addUser(@RequestBody String email, @RequestBody String displayName) {
        logger.debug("Adding user");
        {
            User dbuser = repo.findByEmail(email);
            if (dbuser != null) {
                return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
            }
        }
        String tempToken = UUID.randomUUID().toString() + "." + UUID.randomUUID().toString();
        LocalTime expiresAt = LocalTime.now().plusHours(10);
        User user = new User(email, displayName, null, tempToken, expiresAt);
        repo.save(user);
        String mailBody = EmailTemplate.getValue("inviteuser", user);
        String mailSubject = EmailTemplate.getSubject("cssr.mail.inviteuser.subject",
                inviteUserSubject, user);
        EmailView v = new EmailView().setTo(user.getEmail()).setSubject(mailSubject)
                .setBody(mailBody);
        Email mail = new Email(v);
        emailRepo.save(mail);
        return new ResponseEntity<Long>(user.getUserId(), HttpStatus.OK);
    }

}
