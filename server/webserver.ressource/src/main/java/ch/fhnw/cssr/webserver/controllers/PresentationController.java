package ch.fhnw.cssr.webserver.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.fhnw.cssr.domain.PresentationRepository;
import ch.fhnw.cssr.domain.Presentation;

@RestController
@RequestMapping("/presentation")
public class PresentationController {

	@Autowired
	private PresentationRepository repo;
	

	@RequestMapping(method = RequestMethod.GET)
    public List<Presentation> getFuture() {
        return repo.getAllFuture();
    }

	@RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Presentation> addPresentation(@RequestBody Presentation pres) {
         repo.save(pres);
         return new ResponseEntity<Presentation>(pres, HttpStatus.CREATED);
    }
}
