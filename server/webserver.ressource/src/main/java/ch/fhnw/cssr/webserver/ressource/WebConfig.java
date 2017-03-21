package ch.fhnw.cssr.webserver.ressource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

	@Value("${cors.domain}")
	private String corsDomain;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		if (corsDomain != null && !corsDomain.equals("")) {
			registry.addMapping("/**").allowedOrigins(corsDomain)
					.allowedMethods("PUT", "DELETE", "POST", "GET")
					.allowedHeaders("Authorization")
					.allowCredentials(false)
					.maxAge(3600);
		}
	}
}