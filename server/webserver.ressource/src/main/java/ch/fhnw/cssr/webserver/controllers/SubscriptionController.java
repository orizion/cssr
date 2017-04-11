package ch.fhnw.cssr.webserver.controllers;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.fhnw.cssr.domain.Subscription;
import ch.fhnw.cssr.domain.repository.PresentationRepository;
import ch.fhnw.cssr.domain.repository.SubscriptionRepository;
import ch.fhnw.cssr.domain.Presentation;

@RestController
@RequestMapping("/presentation/{presentationId}/subscription")
public class SubscriptionController {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SubscriptionRepository subscriptionRepo;

    @RequestMapping(method = RequestMethod.GET)
    public List<Subscription> getSubscriptions(
            @PathVariable(name = "presentationId", required = true) int presentationId) {
        logger.debug("Getting subscriptions of {}", presentationId);
        return subscriptionRepo.findByPresentationId(presentationId);
    }

    /**
     * Deletes a subscription
     * @param presentationId The presentation Id. While this information is redundant, it is checked
     * @param subscriptionId The actual subscriptionId
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
     * @param subscription The subscription to modify.
     * @return The modified subscription.
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<Subscription> modifySubscription(@RequestBody Subscription subscription) {
        if (subscription.getSubscriptionId() == null) {
            logger.warn("SubscriptionId not set, use POST instead");
            return new ResponseEntity<Subscription>(HttpStatus.PRECONDITION_FAILED);
        }
        logger.debug("Modifying subscription");
        subscriptionRepo.save(subscription);
        return new ResponseEntity<Subscription>(subscription, HttpStatus.OK);
    }

    /**
     * Adds a new subscription
     * @param presentationId The presentationId
     * @param subscription The new subscription. The presentationId must match
     * @return The new subscription
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Subscription> addSubscription(
            @PathVariable(name = "presentationId", required = true) int presentationId,
            @RequestBody Subscription subscription) {
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
