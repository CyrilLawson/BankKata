package com.appsland.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 *
 * Classe de configuration de swagger pour la documentation de l'API
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.appsland"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .tags(new Tag("Account management", "Gestion de comptes bancaires"));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Documentation des API de gestion de comptes bancaires")
                .description("REST APIs pour la gestion des comptes dans une banque")
                .contact(new Contact("Cyril LAWSON", "", "cyril.lawson2022@gmail.com"))
                .version("1.0")
                .build();
    }
}
