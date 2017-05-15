package ch.fhnw.cssr.webserver.controllers;

import java.time.LocalTime;
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

    /**
     * Adds a new extern user. Will fail when given an existing user of an FHNW email. 
     * @param newUserData The new user
     * @return The Id of the new user.
     */
    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN','ROLE_COORD')")
    public ResponseEntity<Long> addUser(@RequestBody UserAddMeta newUserData) {
        logger.debug("Adding user");
        if (User.isFhnwEmail(newUserData.getEmail())) {
            // Cannot add such a user, use integrated authentication instead
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        }
        { // Just encapsulate scope for bug free code :)
            // This is actually check in DB as well, but we prefer being sure here
            User dbuser = repo.findByEmail(newUserData.getEmail());
            if (dbuser != null) {
                return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
            }
        }
        String tempToken = UUID.randomUUID().toString() + "." + UUID.randomUUID().toString();
        LocalTime expiresAt = LocalTime.now().plusHours(10);
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
