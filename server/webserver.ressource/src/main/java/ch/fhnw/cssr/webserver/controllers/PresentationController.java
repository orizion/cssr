package ch.fhnw.cssr.webserver.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ch.fhnw.cssr.domain.Presentation;
import ch.fhnw.cssr.domain.PresentationRepository;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/presentation")
public class PresentationController {

	@Autowired
	private PresentationRepository repo;

	@RequestMapping(method = RequestMethod.GET)
	@ApiResponse(code = 200, message = "Gets all presentations that have not passed already", response = Presentation.class, responseContainer = "List")
	public List<Presentation> getFuture() {
		return repo.getAllFuture();
	}

	@RequestMapping(method = RequestMethod.GET, path = "{id}")
	@ApiResponses(value = { 
		      @ApiResponse(code = 201, message = "Gets data about a presentation", 
		                   response=Presentation.class,
		                   responseContainer = "List"),
		      @ApiResponse(code = 404, message = "Presentation not found in database") })
	public ResponseEntity<Presentation> getSingle(@PathVariable(name = "id", required = true) Integer id) {
		Presentation resp = repo.findOne(id);
		if (resp == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Presentation>(resp, HttpStatus.FOUND);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<Presentation> modifyPresentation(@RequestBody Presentation pres) {
		if (pres.getPresentationId() == null) {
			return new ResponseEntity<Presentation>(HttpStatus.PRECONDITION_FAILED);
		}
		repo.save(pres);
		return new ResponseEntity<Presentation>(pres, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Presentation> addPresentation(@RequestBody Presentation pres) {
		if (pres.getPresentationId() != null) {
			return new ResponseEntity<Presentation>(HttpStatus.PRECONDITION_FAILED);
		}
		repo.save(pres);
		return new ResponseEntity<Presentation>(pres, HttpStatus.CREATED);
	}
}
