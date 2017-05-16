package ch.fhnw.cssr.webserver.controllers;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ch.fhnw.cssr.domain.Email;
import ch.fhnw.cssr.domain.EmailView;
import ch.fhnw.cssr.domain.User;
import ch.fhnw.cssr.domain.UserAddMeta;
import ch.fhnw.cssr.domain.repository.EmailRepository;
import ch.fhnw.cssr.domain.repository.UserRepository;
import ch.fhnw.cssr.mailutils.EmailTemplate;
import ch.fhnw.cssr.webserver.utils.UserUtils;

@RestController
@RequestMapping("/admin/user")
public class UserAdminController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${cssr.mail.inviteuser.subject}")
    private String inviteUserSubject;

    @Autowired
    private UserRepository repo;
    
    @Autowired
    private UserUtils userUtils;


    @Autowired
    private EmailRepository emailRepo;

    /**
     * Modifies an existing user by setting the display name and or the roleId
     * @param userData The user to be modified. Must have a user id
     * @return Returns the modified user
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<User> modifyUser(@RequestBody User userData) {
        logger.debug("Modifing user");
        if (userData.getUserId() == null || userData.getUserId() == 0) {
            logger.warn("User id not set, use POST instead");
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        }
        User existingUser = repo.findOne(userData.getUserId());
        if (existingUser == null) {
            logger.error("User not found: {}", userData.getUserId());
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        }
        if (!existingUser.getEmail().equals(userData.getEmail())) {
            logger.error("Email not matching", userData.getUserId());
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        }
        existingUser.setDisplayName(userData.getDisplayName());
        existingUser.setRoleId(userData.getRoleId());
        repo.save(existingUser);
        return new ResponseEntity<User>(existingUser, HttpStatus.OK);
    }

    /**
     * Adds a new extern user. Will fail when given an existing user of an FHNW email.
     * 
     * @param newUserData
     *            The new user
     * @return The Id of the new user.
     */
    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_COORD')")
    public ResponseEntity<Long> addUser(@RequestBody UserAddMeta newUserData) {
        logger.debug("Adding user");
        
        if (User.isFhnwEmail(newUserData.getEmail())) {
            // We just make the user persistent.
            userUtils.assureCreated(newUserData.getEmail());
            User dbuser = repo.findByEmail(newUserData.getEmail());
            return new ResponseEntity<>(dbuser.getUserId(), HttpStatus.OK);

        } else {
            // This is actually check in DB as well, but we prefer being sure here
            User dbuser = repo.findByEmail(newUserData.getEmail());
            if (dbuser != null) {
                return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
            }
        }
        String tempToken = UUID.randomUUID().toString() + "." + UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(10);
        User user = new User(newUserData.getEmail(), newUserData.getDisplayName(), null, tempToken,
                expiresAt);
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
