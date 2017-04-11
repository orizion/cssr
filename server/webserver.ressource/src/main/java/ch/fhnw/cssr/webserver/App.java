package ch.fhnw.cssr.webserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import ch.fhnw.cssr.security.CustomPasswordEncoder;

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


}
