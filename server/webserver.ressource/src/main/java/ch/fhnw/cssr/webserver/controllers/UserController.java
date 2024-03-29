package ch.fhnw.cssr.webserver.controllers;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
@RequestMapping("/user")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${cssr.mail.resetpassword.subject}")
    private String resetPasswordSubject;

    @Autowired
    private UserRepository repo;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private EmailRepository emailRepo;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

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
     * Get a temporary token for the user.
     * 
     * @param user
     *            The user logged in.
     * @return A Token that can be used as ?temptoken= parameter
     */
    @RequestMapping(method = RequestMethod.GET, path = "me/tempToken")
    public ResponseEntity<String> getTemporaryToken(Principal user) {
        User dbuser = repo.findByEmail(user.getName());
        String tempToken = UUID.randomUUID().toString() + "." + UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(10);
        dbuser.setTempToken(tempToken, expiresAt);
        repo.save(dbuser);
        return new ResponseEntity<String>(tempToken, HttpStatus.OK);
    }

    /**
     * Sets the password for the currently logged in user.
     * @param resetPwdParameter The reset password parameters
     * @return Returns false if the password does not match and true if it worked.
     */
    @RequestMapping(method = RequestMethod.PUT, path = "me/password")
    public ResponseEntity<Boolean> resetPassword(
            @RequestBody ResetPasswordParameters resetPwdParameter) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email;
        if (resetPwdParameter.isOldPasswordTempToken()) {
            User us = repo.findByTempToken(resetPwdParameter.getOldPassword());
            if (us == null) {
                return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
            }
            if (us.getTempTokenExpiresAt().compareTo(LocalDateTime.now()) < 0) {
                return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
            }
            email = us.getEmail();
        } else if (auth == null) {
            logger.warn("User not logged in and no temp token");
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        } else {
            email = auth.getName();
            Authentication auth2 = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(email,
                            resetPwdParameter.getOldPassword(), new ArrayList<GrantedAuthority>()));
            if (!auth2.isAuthenticated()) {
                logger.warn("Old password not matching");
                return new ResponseEntity<Boolean>(false, HttpStatus.PRECONDITION_FAILED);   
            }
        }
        String newPassword = this.passwordEncoder.encode(resetPwdParameter.getNewPassword());
        User us = repo.findByEmail(email);
        us.setPasswordEnc(newPassword);
        repo.save(us);
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    /**
     * Resets the password of the current user by sending a temporary token by mail.
     * 
     * @param user
     *            The user
     * @return The mail of the user
     */
    @RequestMapping(method = RequestMethod.POST, path = "me/sendResetPassword")
    public ResponseEntity<String> sendResetPasswordMail(Principal user) {
        logger.debug("Resetting password");
        if (User.isFhnwEmail(user.getName())) {
            logger.warn("User cannot reset password. This has to be done in AD");
            return new ResponseEntity<String>(HttpStatus.PRECONDITION_FAILED);
        }
        User dbuser = repo.findByEmail(user.getName());
        String tempToken = UUID.randomUUID().toString() + "." + UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(10);
        dbuser.setTempToken(tempToken, expiresAt);
        repo.save(dbuser);
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("u", dbuser);
        String mailBody = EmailTemplate.getValue("resetPassword", values);
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
            logger.warn("Invalid credentials for user: {} ", creds.getEmail());
            return new ResponseEntity<TokenResult>(HttpStatus.UNAUTHORIZED);
        }
        // Make the user persistent
        if (User.isFhnwEmail(creds.getEmail())) {
            userUtils.assureCreated(creds.getEmail());
        }
        TokenResult token = TokenAuthenticationService.getJwtTokenResult(auth.getAuthorities(),
                auth.getName());
        return new ResponseEntity<TokenResult>(token, HttpStatus.OK);
    }
}
