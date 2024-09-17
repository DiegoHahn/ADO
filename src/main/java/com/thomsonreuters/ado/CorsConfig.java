package com.thomsonreuters.ado;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // permite CORS em todas as rotas
                        .allowedOriginPatterns("*") // permite todas as origens
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("*") // permite todos os headers
                        .allowCredentials(true); // permite credentials
            }
        };
    }
}

