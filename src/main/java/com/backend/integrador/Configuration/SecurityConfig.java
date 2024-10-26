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
            .cors(cors -> cors.disable()) 
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(authorize -> authorize
                // Permitir acceso sin autenticación a productos, categorías, contacto y registro de usuarios
                .requestMatchers("/api/productos/**").permitAll()
                .requestMatchers("/api/categorias/**").permitAll()
                .requestMatchers("/api/contacto/**").permitAll()
                .requestMatchers("/api/usuarios/registrar").permitAll() // Permitir registrar usuarios sin autenticación
                .requestMatchers("/api/auth/**").permitAll() // Permitir autenticación (login)
                .anyRequest().authenticated() // Proteger cualquier otra ruta
            );
        return http.build();
    }

    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }
}
