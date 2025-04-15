package org.bitebuilders.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "CRM module for Uralintern",
                version = "1.0",
                description = "API documentation for Uralintern CRM Module"
        ),
        security = @SecurityRequirement(name = "BearerAuth")
)
@SecurityScheme(
        name = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig implements WebMvcConfigurer { // Добавлено implements WebMvcConfigurer
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // Настраиваем путь к вашему статическому swagger.json
                registry.addResourceHandler("/static/swagger.json")
                        .addResourceLocations("classpath:/static/swagger.json");
        }
}


