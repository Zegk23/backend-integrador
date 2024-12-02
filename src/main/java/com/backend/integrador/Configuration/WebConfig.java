package com.backend.integrador.Configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Aplica CORS solo a rutas que empiezan con /api
                .allowedOrigins(
                        "http://localhost:3000", // Origen para desarrollo
                        "https://front-integrador-velazco.vercel.app/",
                        "https://velazco-pagina.vercel.app/" // Origen para producción (actualízalo cuando subas el frontend)
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP permitidos
                .allowedHeaders("*") // Permitir todos los encabezados
                .allowCredentials(true); // Permitir cookies o credenciales si es necesario
    }
}
