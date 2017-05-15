package ch.fhnw.cssr.webserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import ch.fhnw.cssr.security.CustomPasswordEncoder;
import ch.fhnw.cssr.security.EwsAuthenticator;
import ch.fhnw.cssr.webserver.utils.UserUtils;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
    

    @Bean 
    @Primary
    public CustomPasswordEncoder passwordEncoder() {
        return new CustomPasswordEncoder();
    }

    @Bean 
    @Primary
    public EwsAuthenticator ewsAuthenticator() {
        return new EwsAuthenticator();
    }
    

    @Bean 
    @Primary
    public UserUtils userUtils() {
        return new UserUtils();
    }

}
