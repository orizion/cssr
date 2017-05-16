package ch.fhnw.cssr.webserver;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
@EnableWebMvc
@EntityScan("ch.fhnw.cssr")
@ComponentScan("ch.fhnw.cssr")
@EnableJpaRepositories(basePackages = { "ch.fhnw.cssr.domain" })
public class WebConfig extends WebMvcConfigurerAdapter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${cssr.cors.domain}")
    private String corsDomain;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (corsDomain != null && !corsDomain.equals("")) {
            logger.info("Cors for domain {}", corsDomain);
            registry.addMapping("/**")
                   .allowedOrigins(corsDomain)
                   .allowedMethods("PUT", "DELETE", "POST", "GET")
                   .allowedHeaders("Authorization", "Content-Type", "X-Request-With")
                   .allowCredentials(true) 
                   .maxAge(3600);
            
        } else {
            logger.info("no cors");
        }
    }
    


    /**
     * This is hack to make dates in JSON decent/usable (meaning Standard conform).
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter jsonMessageConverter = 
                        (MappingJackson2HttpMessageConverter) converter;
                ObjectMapper objectMapper = jsonMessageConverter.getObjectMapper();
                objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                break;
            }
        }
    }

}