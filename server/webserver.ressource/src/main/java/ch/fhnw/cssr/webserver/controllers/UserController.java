package ch.fhnw.cssr.webserver.controllers;

import java.security.Principal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ch.fhnw.cssr.domain.Email;
import ch.fhnw.cssr.domain.EmailRepository;
import ch.fhnw.cssr.domain.User;
import ch.fhnw.cssr.domain.UserRepository;
import ch.fhnw.cssr.mailutils.EmailTemplate;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository repo;

    @Autowired
    private EmailRepository emailRepo;

    /**
     * Gets all users in the database.
     * @return A list of all users
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<User> getAll() {
        ArrayList<User> ls = new ArrayList<>();
        Iterable<User> users = repo.findAll();
        users.forEach(ls::add);
        return ls;
    }

    /**
     * Resets the password of the current user by sending a temporary token by mail.
     * @param user The user
     * @return The mail of the user
     */
    @RequestMapping(method = RequestMethod.POST, path = "me/resetPassword")
    public ResponseEntity<String> resetPassword(Principal user) {
        User dbuser = repo.findByEmail(user.getName());
        String tempToken = UUID.randomUUID().toString() + "." + UUID.randomUUID().toString();
        LocalTime expiresAt = LocalTime.now().plusHours(10);
        dbuser.setTempToken(tempToken, expiresAt);
        repo.save(dbuser);
        String mailBody = EmailTemplate.getValue("resetPassword", dbuser);
        Email mail = new Email(dbuser.getEmail(), null, null, "Reset password", mailBody);
        emailRepo.save(mail);
        return new ResponseEntity<String>(dbuser.getEmail(), HttpStatus.OK);
    }
}
