package ch.fhnw.cssr.mailer;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Just a simple utility for sending mails.
 *
 */
@ComponentScan("ch.fhnw.cssr")
@EnableJpaRepositories(basePackages = { "ch.fhnw.cssr.domain" })
@SpringBootApplication
@EntityScan("ch.fhnw.cssr")
public class App {
    
    /** 
     * Main entry for the application which runs Spring.
     * */ 
    public static void main(String[] args) {
        SpringApplicationBuilder build = new SpringApplicationBuilder(App.class);
        build.web(false);

        try {
            build.run(args);
        } catch (Exception err) {
            err.printStackTrace();
            System.err.println(err.toString());
        }
    }
}
