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
@RequestMapping("/user")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${cssr.mail.resetpassword.subject}")
    private String resetPasswordSubject;

    @Autowired
    private UserRepository repo;

    @Autowired
    private EmailRepository emailRepo;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Gets details of the current user.
     * 
     * @return An object containing the user name and it' roles
     */
    @RequestMapping(method = RequestMethod.GET, path = "me")
    public UserMeta get(Principal user) {
        logger.debug("Getting current user");
        UserDetails dt = userDetailsService.loadUserByUsername(user.getName());
        boolean isExtern = true;
        Long userId = null;
        if (dt instanceof User) {
            isExtern = ((User) dt).isExtern();
            userId = ((User) dt).getUserId();
        }
        String[] roles = dt.getAuthorities().stream().map(a -> a.getAuthority())
                .toArray(String[]::new);
        UserMeta meta = new UserMeta(isExtern, dt.getUsername(), roles, userId);
        return meta;
    }

    /**
     * Gets all users in the database.
     * 
     * @return A list of all users
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<User> getAll(
            @RequestParam(name = "searchString", required = false, defaultValue = "") 
            String searchString) {
        logger.debug("Searching for users");
        ArrayList<User> ls = new ArrayList<>();
        Iterable<User> users = searchString == null || searchString.equals("") ? repo.findAll()
                : repo.findByEmailOrDisplayName(searchString);
        users.forEach(ls::add);
        return ls;
    }

    /**
     * Resets the password of the current user by sending a temporary token by mail.
     * 
     * @param user
     *            The user
     * @return The mail of the user
     */
    @RequestMapping(method = RequestMethod.POST, path = "me/resetPassword")
    public ResponseEntity<String> resetPassword(Principal user) {
        logger.debug("Resetting password");

        User dbuser = repo.findByEmail(user.getName());
        String tempToken = UUID.randomUUID().toString() + "." + UUID.randomUUID().toString();
        LocalTime expiresAt = LocalTime.now().plusHours(10);
        dbuser.setTempToken(tempToken, expiresAt);
        repo.save(dbuser);
        String mailBody = EmailTemplate.getValue("resetPassword", dbuser);
        String mailSubject = EmailTemplate.getSubject("cssr.mail.resetpassword.subject",
                resetPasswordSubject, user);
        EmailView v = new EmailView().setTo(dbuser.getEmail()).setSubject(mailSubject)
                .setBody(mailBody);
        Email mail = new Email(v);
        emailRepo.save(mail);
        return new ResponseEntity<String>(dbuser.getEmail(), HttpStatus.OK);
    }

    /**
     * Use this method to login the user.
     * 
     * @param creds
     *            The credentials
     * @return The tokenResult
     */
    @RequestMapping(method = RequestMethod.POST, path = "/login")
    public ResponseEntity<TokenResult> login(@RequestBody AccountCredentials creds,
            HttpServletRequest request) {
        if (request.getMethod().toUpperCase().equals("OPTIONS")) {
            return new ResponseEntity<TokenResult>(HttpStatus.OK);
        }

        Authentication auth = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(creds.getEmail(),
                        creds.getPassword(), new ArrayList<GrantedAuthority>()));
        if (!auth.isAuthenticated()) {
            return new ResponseEntity<TokenResult>(HttpStatus.UNAUTHORIZED);
        }
        TokenResult token = TokenAuthenticationService.getJwtTokenResult(auth.getAuthorities(),
                auth.getName());
        return new ResponseEntity<TokenResult>(token, HttpStatus.OK);
    }
}
