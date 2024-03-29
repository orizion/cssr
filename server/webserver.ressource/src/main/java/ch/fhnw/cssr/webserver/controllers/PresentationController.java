package ch.fhnw.cssr.webserver.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.fhnw.cssr.domain.Presentation;
import ch.fhnw.cssr.domain.Role;
import ch.fhnw.cssr.domain.Subscription;
import ch.fhnw.cssr.domain.User;
import ch.fhnw.cssr.domain.repository.PresentationRepository;
import ch.fhnw.cssr.domain.repository.SubscriptionRepository;
import ch.fhnw.cssr.domain.repository.UserRepository;
import ch.fhnw.cssr.webserver.utils.UserUtils;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/presentation")
public class PresentationController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PresentationRepository repo;

    @Autowired
    private SubscriptionRepository subscription;

    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private UserUtils userUtils;
    
    
    @Autowired
    private UserDetailsService userDetails;

    /**
     * Gets all presentations that are optionally always in the future.
     * 
     * @param futureOnly
     *            True to only get future presentations.
     * @return A List of presentations.
     */
    @RequestMapping(method = RequestMethod.GET)
    @ApiResponse(code = 200, message = "Gets all presentations that have not passed already")
    public List<Presentation> findAll(
            @RequestParam(name = "futureOnly", required = true) boolean futureOnly) {

        if (futureOnly) {
            logger.debug("Find future presentations");
            return repo.getAllFuture();
        }
        logger.debug("Find all presentations");
        ArrayList<Presentation> presentations = new ArrayList<Presentation>();
        for (Presentation p : repo.findAll()) {
            presentations.add(p);
        }
        
        return presentations;
    }

    /**
     * Gets all presentations of current user
     * 
     * @param user
     *            The logged in user
     * @return A List of presentations.
     */
    @RequestMapping(method = RequestMethod.GET, path = "me")
    @ApiResponse(code = 200, message = "Gets all presentations of current user")
    public List<Integer> findMine(Principal user) {
        userUtils.assureCreated(user.getName());
        User dbUser = userRepo.findByEmail(user.getName());
        List<Subscription> subs = subscription.findByUser(dbUser);
        return subs.stream().map(f -> f.getPresentationId()).collect(Collectors.toList());
    }

    
    /**
     * Gets a single presentation, without files or subscriptions.
     * 
     * @param id
     *            The presentationId
     * @return A presentation, or 404 if not found
     */
    @RequestMapping(method = RequestMethod.GET, path = "{id}")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Gets data about a presentation"),
            @ApiResponse(code = 404, message = "Presentation not found in database") })
    public ResponseEntity<Presentation> getSingle(
            @PathVariable(name = "id", required = true) Integer id) {
        Presentation resp = repo.findOne(id);
        if (resp == null) {
            logger.warn("Presentation not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Presentation>(resp, HttpStatus.OK);
    }

    /**
     * Modifies an EXISISTING presentation.
     * 
     * @param pres
     *            The presentation.
     * @return The modified presentation as seen on the database.
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<Presentation> modifyPresentation(@RequestBody Presentation pres,
            Principal user) {
        if (pres.getPresentationId() == null) {
            logger.warn("Presentation Id is null, use POST instead");
            return new ResponseEntity<Presentation>(HttpStatus.PRECONDITION_FAILED);
        }
        UserDetails dt = userDetails.loadUserByUsername(user.getName());
        String roleCoord = Role.getDefaultRoleName(Role.ROLE_COORD);
        String roleAdmin = Role.getDefaultRoleName(Role.ROLE_ADMIN);
        boolean isPermitted = dt.getAuthorities().stream().map(a -> a.getAuthority())
                .anyMatch(p -> p.equals(roleCoord) || p.equals(roleAdmin));
        if (!isPermitted && dt instanceof User) {
            Presentation existing = repo.findOne(pres.getPresentationId());
            if (existing.getSpeakerId() == ((User) dt).getUserId()) {
                isPermitted = true;
                // The speaker may not edited date and location
                if (!pres.getDateTime().equals(existing.getDateTime())) {
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
                if (!pres.getLocation().equals(existing.getLocation())) {
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }
        }
        if (!isPermitted) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        repo.save(pres);
        return new ResponseEntity<Presentation>(pres, HttpStatus.OK);
    }

    /**
     * Adds a presentation. PresentationId MUST be null.
     * 
     * @param pres
     *            The presentation
     * @return The new presentation with id.
     */
    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_COORD')")
    public ResponseEntity<Presentation> addPresentation(@RequestBody Presentation pres) {
        if (pres.getPresentationId() != null && pres.getPresentationId() != Integer.valueOf(0)) {
            logger.warn("Presentation Id is not null, use PUT instead");
            return new ResponseEntity<Presentation>(HttpStatus.PRECONDITION_FAILED);
        }
        repo.save(pres);
        return new ResponseEntity<Presentation>(pres, HttpStatus.CREATED);
    }

}
