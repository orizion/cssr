package ch.fhnw.cssr.webserver.controllers;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    // TODO: Remove this class
    
    /**
     * Not used.
     * @param name Not used
     * @return Not used
     */
    @RequestMapping(method = RequestMethod.GET)
    public String getTest(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hello " + name;
    }

    /**
     * Not used.
     * @return not used
     */
    @RequestMapping(method = RequestMethod.GET, path = "date")
    public LocalDateTime getNow() {
        return LocalDateTime.now();
    }

    /**
     * Not used.
     * @param user Not used.
     * @return Not used.
     */
    @RequestMapping(method = RequestMethod.GET, path = "user")
    public Principal getUser(Principal user) {
        return user;
    }
}
