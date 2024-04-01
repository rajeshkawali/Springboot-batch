package com.rajeshkawali.config;

import static org.springdoc.core.utils.Constants.ALL_PATTERN;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;


/**
 * @author Rajesh_Kawali
 */
@Configuration
public class OpenApiConfig {

    @Bean
    @Profile("!prod")
    public GroupedOpenApi actuatorApi() {
        return GroupedOpenApi.builder()
                .group("Actuator")
                .pathsToMatch(ALL_PATTERN)
                .addOpenApiCustomizer(openApi -> openApi.info(new Info()
						.title("Customer API")
                        .description("Customer sample application")
                        .version("1.0")
                        .license(new License().name("Currencies Direct").url("https://www.currenciesdirect.com/en"))))
                .pathsToExclude("/health/*").build();
    }

    @Bean
    public GroupedOpenApi usersGroup() {
        return GroupedOpenApi.builder().group("customers").addOperationCustomizer((operation, handlerMethod) -> {
                    operation.addSecurityItem(new SecurityRequirement().addList("basicScheme"));
                    return operation;
                }).addOpenApiCustomizer(openApi -> openApi.info(new Info()
						.title("Customer API")
                        .description("Customer sample application")
                        .version("1.0")
                        .license(new License().name("Currencies Direct").url("https://www.currenciesdirect.com/en"))))
                .packagesToScan("com.rajeshkawali").build();
    }
}