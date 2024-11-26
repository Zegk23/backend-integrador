package com.backend.integrador.Configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // Habilitar CORS solo para las rutas de la API
                .allowedOrigins("http://localhost:3000")  // Asegúrate de que sea el origen correcto de tu frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")  // Permitir todos los encabezados
                .allowCredentials(true);  // Permitir el envío de cookies
    }
}
