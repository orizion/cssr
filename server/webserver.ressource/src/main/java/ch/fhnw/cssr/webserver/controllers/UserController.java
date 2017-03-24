package ch.fhnw.cssr.webserver.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ch.fhnw.cssr.domain.Presentation;
import ch.fhnw.cssr.domain.User;
import ch.fhnw.cssr.domain.UserRepository;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository repo;
	

	@RequestMapping(method = RequestMethod.GET)
    public Iterable<User> getAll() {
        return repo.findAll();
    }
}
