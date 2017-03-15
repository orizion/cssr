package ch.fhnw.cssr.webserver.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan("ch.fhnw.cssr")
@EnableJpaRepositories(basePackages = {"ch.fhnw.cssr.domain"})
@SpringBootApplication
@EntityScan("ch.fhnw.cssr")
public class CssrApplication {

	public static void main(String[] args) {
		SpringApplication.run(CssrApplication.class, args);
	}
}
