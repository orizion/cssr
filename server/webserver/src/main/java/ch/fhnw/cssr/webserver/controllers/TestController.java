package ch.fhnw.cssr.webserver.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

	@RequestMapping(method = RequestMethod.GET)
    public String GetTest(@RequestParam(value="name", defaultValue="World") String name) {
        return "Hello " + name;
    }
	
}
