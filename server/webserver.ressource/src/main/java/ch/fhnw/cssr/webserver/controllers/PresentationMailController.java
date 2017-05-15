package ch.fhnw.cssr.webserver.controllers;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ch.fhnw.cssr.domain.Email;
import ch.fhnw.cssr.domain.EmailView;
import ch.fhnw.cssr.domain.Presentation;
import ch.fhnw.cssr.domain.Subscription;
import ch.fhnw.cssr.domain.User;
import ch.fhnw.cssr.domain.repository.EmailRepository;
import ch.fhnw.cssr.domain.repository.PresentationRepository;
import ch.fhnw.cssr.domain.repository.SubscriptionRepository;
import ch.fhnw.cssr.mailutils.EmailTemplate;

@RestController
@RequestMapping("/presentation")
public class PresentationMailController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PresentationRepository repo;

    @Autowired
    private SubscriptionRepository subscriptionRepo;

    @Value("${cssr.mail.invitation.target}")
    private String invitationTarget;

    @Value("${cssr.mail.invitation.subject}")
    private String invitationSubject;

    @Autowired
    private EmailRepository emailRepo;

    /**
     * Gets the template for the invitation.
     * 
     * @param id
     *            The id of the presentation
     * @return The Email that would be sent
     */
    @RequestMapping(method = RequestMethod.GET, path = "{id}/invitation/template")
    @PreAuthorize("hasRole('ROLE_ADMIN', 'ROLE_SGL', 'ROLE_COORD')")
    public ResponseEntity<EmailView> getInvitationMailTemplate(
            @PathVariable(name = "id", required = true) Integer id) {
        Presentation resp = repo.findOne(id);
        if (resp == null) {
            logger.warn("Presentation not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String mailBody = EmailTemplate.getValue("sendInvitation", resp);
        String mailSubject = EmailTemplate.getSubject("cssr.mail.invitation.subject",
                invitationSubject, resp);
        String to = invitationTarget;
        Collection<Subscription> subscriptions = subscriptionRepo.findByPresentationId(id);

        // Send invitation mail to registred external users as well
        String additionalUsers = subscriptions.stream().map(s -> s.getUser())
                .filter(u -> u.isExtern()).map(u -> u.getEmail()).collect(Collectors.joining(";"));
        if (additionalUsers != null && !additionalUsers.equals("")) {
            to = ";" + additionalUsers;
        }

        EmailView v = new EmailView().setTo(to).setSubject(mailSubject).setBody(mailBody);
        return new ResponseEntity<EmailView>(v, HttpStatus.OK);
    }

    /**
     * Sends the invitation mail for a presentation.
     * 
     * @param id
     *            The presentation Id.
     * @param mail
     *            The mail to be sent
     * @return The presentation that a mail was sent for.
     */
    @RequestMapping(method = RequestMethod.POST, path = "{id}/invitation/send")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SGL', 'ROLE_COORD')")
    public ResponseEntity<Email> sendInvitationMail(
            @PathVariable(name = "id", required = true) Integer id, @RequestBody EmailView mail) {
        Email dbmail = new Email(mail);
        emailRepo.save(dbmail);
        return new ResponseEntity<Email>(dbmail, HttpStatus.FOUND);
    }
}
