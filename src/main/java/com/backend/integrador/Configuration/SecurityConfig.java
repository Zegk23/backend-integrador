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
                .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF temporalmente
                .authorizeHttpRequests(authorize -> authorize
                        // Permitir acceso público a ciertas rutas
                        .requestMatchers("/api/productos/**").permitAll()
                        .requestMatchers("/api/categorias/**").permitAll()
                        .requestMatchers("/api/contacto/**").permitAll()
                        .requestMatchers("/api/usuarios/registrar").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/historial-pedidos/**").permitAll() // Cambiado a acceso público
                        .requestMatchers("/api/pedidos/**").permitAll() // Cambiado a acceso público
                        
                        // Requiere autenticación para cualquier otra solicitud
                        .anyRequest().authenticated())
                .cors(cors -> cors.configure(http)); // Asegúrate de habilitar la configuración de CORS
        return http.build();
    }
}
