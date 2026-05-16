package hr.fer.rest_api.config;

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
                registry.addMapping("/api/**") // Primijeni na sve rute koje počinju s /api
                        .allowedOrigins("*")  // Dopusti pristup sa svih adresa (frontenda)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // Dopusti sve HTTP metode
                        .allowedHeaders("*"); // Dopusti sva zaglavlja
            }
        };
    }
}

