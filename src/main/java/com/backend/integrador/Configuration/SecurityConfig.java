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
                .csrf(csrf -> csrf.disable()) // Desactivar CSRF (opcional durante desarrollo)
                .authorizeHttpRequests(authorize -> authorize
                        // Permitir acceso sin autenticación a ciertas rutas de la API
                        .requestMatchers("/api/productos/**").permitAll()
                        .requestMatchers("/api/categorias/**").permitAll()
                        .requestMatchers("/api/contacto/**").permitAll()
                        .requestMatchers("/api/usuarios/registrar").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/historial-pedidos/**").permitAll() // Permitir acceso a historial-pedidos
                        .requestMatchers("/api/pedidos/**").permitAll() // Permitir acceso completo a pedidos
                        .requestMatchers("/api/historial-pedidos").permitAll()
                        .anyRequest().authenticated()); // Requiere autenticación para las demás rutas
        return http.build();
    }
}
