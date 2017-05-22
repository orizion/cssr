package ch.fhnw.cssr.webserver.controllers;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ch.fhnw.cssr.domain.Subscription;
import ch.fhnw.cssr.domain.User;
import ch.fhnw.cssr.domain.repository.SubscriptionRepository;
import ch.fhnw.cssr.domain.repository.UserRepository;
import ch.fhnw.cssr.webserver.utils.UserUtils;

@RestController
@RequestMapping("/presentation/{presentationId}/subscription")
public class SubscriptionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SubscriptionRepository subscriptionRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserUtils userUtils;

    /** 
     * Gets the subscriptions for a presentation.
     * @param presentationId The id of the presentation.
     * @return The subscriptions
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Subscription> getSubscriptions(
            @PathVariable(name = "presentationId", required = true) int presentationId) {
        logger.debug("Getting subscriptions of {}", presentationId);
        List<Subscription> subs = subscriptionRepo.findByPresentationId(presentationId);
        subs.forEach(c -> { // Delete private data
            c.getUser().setPasswordEnc(null);
            c.getUser().setTempToken(null, null);
        });
        return subs;
    }

    /**
     * Deletes a subscription
     * 
     * @param presentationId
     *            The presentation Id. While this information is redundant, it is checked
     * @param subscriptionId
     *            The actual subscriptionId
     * @return The deleted subscription
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "{subscriptionId}")
    public ResponseEntity<Subscription> deleteSingle(
            @PathVariable(name = "presentationId", required = true) int presentationId,
            @PathVariable(name = "subscriptionId", required = true) long subscriptionId) {
        Subscription subscription = subscriptionRepo.findOne(subscriptionId);
        if (subscription == null) {
            logger.warn("Subscription {} not found", subscription);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (subscription.getPresentationId() != presentationId) {
            logger.warn("PresentationId do not match: {} vs {}", subscription.getPresentationId(),
                    presentationId);
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        }
        logger.debug("Deleting subscription");
        subscriptionRepo.delete(subscription);
        return new ResponseEntity<Subscription>(subscription, HttpStatus.OK);
    }

    /**
     * Modifies an existing Subscription.
     * 
     * @param subscription
     *            The subscription to modify.
     * @return The modified subscription.
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<Subscription> modifySubscription(@RequestBody Subscription subscription) {
        if (subscription.getSubscriptionId() == null) {
            logger.warn("SubscriptionId not set, use POST instead");
            return new ResponseEntity<Subscription>(HttpStatus.PRECONDITION_FAILED);
        }
        Subscription existingSubscription = subscriptionRepo.findOne(
                subscription.getSubscriptionId());
        
        if (existingSubscription.getUser().getUserId() != subscription.getUser().getUserId()) {
            logger.warn("UserId must not be changed");
            return new ResponseEntity<Subscription>(HttpStatus.PRECONDITION_FAILED);
        }
        
        if (existingSubscription.getPresentationId() != subscription.getPresentationId()) {
            logger.warn("PresentationId must not be changed");
            return new ResponseEntity<Subscription>(HttpStatus.PRECONDITION_FAILED);
        }
        
        logger.debug("Modifying subscription");
        subscriptionRepo.save(subscription);
        return new ResponseEntity<Subscription>(subscription, HttpStatus.OK);
    }

    /**
     * Adds a new subscription
     * 
     * @param presentationId
     *            The presentationId
     * @param subscription
     *            The new subscription. The presentationId must match
     * @return The new subscription
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Subscription> addSubscription(
            @PathVariable(name = "presentationId", required = true) int presentationId,
            @RequestBody Subscription subscription, Principal user) {

        if (User.isFhnwEmail(user.getName())) {
            userUtils.assureCreated(user.getName());
        }
        User dbuser = userRepo.findByEmail(user.getName());

        if (subscription.getUser() == null) {
            subscription.setUser(dbuser);
        } else if (subscription.getUser().getUserId() != dbuser.getUserId()) {
            logger.warn("can only subscript oneself");
            return new ResponseEntity<Subscription>(HttpStatus.PRECONDITION_FAILED);
        }
        if (subscription.getSubscriptionId() != null) {
            logger.warn("SubscriptionId set, use PUT instead");
            return new ResponseEntity<Subscription>(HttpStatus.PRECONDITION_FAILED);
        }
        if (subscription.getPresentationId() != presentationId) {
            logger.warn("PresentationId do not match, {} vs {}", subscription.getPresentationId(),
                    presentationId);
            return new ResponseEntity<Subscription>(HttpStatus.PRECONDITION_FAILED);
        }
        logger.debug("Adding subscription");
        subscriptionRepo.save(subscription);
        return new ResponseEntity<Subscription>(subscription, HttpStatus.CREATED);
    }
}
