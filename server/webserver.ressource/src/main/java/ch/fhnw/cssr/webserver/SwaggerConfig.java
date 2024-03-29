package ch.fhnw.cssr.webserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Predicates;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    /**
     * Gets the info for Swagger / OpenAPI.
     * 
     * @return A Docket type of the info.
     */
    @Bean
    public Docket newsApi() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
                .apis(Predicates
                        .not(RequestHandlerSelectors.basePackage("org.springframework.boot")))
                
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("CSSR Backend")
                .description("A REST backend for the CSSR application")
                .version("0.1")
                .build();
    }

}
