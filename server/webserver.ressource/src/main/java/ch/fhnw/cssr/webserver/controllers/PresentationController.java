package ch.fhnw.cssr.webserver.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.fhnw.cssr.domain.Presentation;
import ch.fhnw.cssr.domain.repository.PresentationRepository;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/presentation")
public class PresentationController {

    @Autowired
    private PresentationRepository repo;

    /**
     * Gets all presentations that are optionally always in the future.
     * @param futureOnly True to only get future presentations.
     * @return A List of presentations.
     */
    @RequestMapping(method = RequestMethod.GET)
    @ApiResponse(code = 200, message = "Gets all presentations that have not passed already")
    public List<Presentation> findAll(
            @RequestParam(name = "futureOnly", required = true) boolean futureOnly) {

        if (futureOnly) {
            return repo.getAllFuture();
        }
        ArrayList<Presentation> presentations = new ArrayList<Presentation>();
        for (Presentation p : repo.findAll()) {
            presentations.add(p);
        }
        return presentations;
    }

    /**
     * Gets a single presentation, without files or subscriptions.
     * 
     * @param id
     *            The presentationId
     * @return A presentation, or 404 if not found
     */
    @RequestMapping(method = RequestMethod.GET, path = "{id}")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Gets data about a presentation"),
            @ApiResponse(code = 404, message = "Presentation not found in database") })
    public ResponseEntity<Presentation> getSingle(
            @PathVariable(name = "id", required = true) Integer id) {
        Presentation resp = repo.findOne(id);
        if (resp == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Presentation>(resp, HttpStatus.FOUND);
    }

    /**
     * Modifies an EXISISTING presentation.
     * 
     * @param pres
     *            The presentation.
     * @return The modified presentation as seen on the database.
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<Presentation> modifyPresentation(@RequestBody Presentation pres) {
        if (pres.getPresentationId() == null) {
            return new ResponseEntity<Presentation>(HttpStatus.PRECONDITION_FAILED);
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
    public ResponseEntity<Presentation> addPresentation(@RequestBody Presentation pres) {
        if (pres.getPresentationId() != null) {
            return new ResponseEntity<Presentation>(HttpStatus.PRECONDITION_FAILED);
        }
        repo.save(pres);
        return new ResponseEntity<Presentation>(pres, HttpStatus.CREATED);
    }
}
