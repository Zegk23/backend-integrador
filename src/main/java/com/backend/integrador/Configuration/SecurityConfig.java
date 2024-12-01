package com.backend.integrador.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) 
                .authorizeHttpRequests(authorize -> authorize
                        // Permitir acceso público a ciertas rutas
                        .requestMatchers("/api/productos/**").permitAll()
                        .requestMatchers("/api/categorias/**").permitAll()
                        .requestMatchers("/api/contacto/**").permitAll()
                        .requestMatchers("/api/usuarios/registrar").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        
                        // Proteger rutas sensibles
                        .requestMatchers("/api/historial-pedidos/**").authenticated()
                        .requestMatchers("/api/pedidos/**").authenticated()
                        
                        // Requiere autenticación para cualquier otra solicitud
                        .anyRequest().authenticated())
                .cors(cors -> cors.configure(http)); // Asegúrate de habilitar la configuración de CORS
        return http.build();
    }
}
