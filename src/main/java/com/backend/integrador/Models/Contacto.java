package com.backend.integrador.Models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.common.base.Preconditions;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "contacto")
public class Contacto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "correo", nullable = false)
    private String correo;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "mensaje", nullable = false)
    private String mensaje;

    @Column(name = "fecha_envio", nullable = false)
    private String fechaEnvio;  
    
    @PrePersist
    protected void onCreate() {
        
        // Validación con Google Guava
        Preconditions.checkNotNull(nombre, "El nombre no puede ser nulo");
        Preconditions.checkArgument(nombre.length() <= 50, "El nombre no puede tener más de 50 caracteres");

        Preconditions.checkNotNull(correo, "El correo electrónico no puede ser nulo");
        Preconditions.checkArgument(correo.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"), "El correo electrónico no es válido");

        Preconditions.checkNotNull(mensaje, "El mensaje no puede ser nulo");
        Preconditions.checkArgument(mensaje.length() <= 500, "El mensaje no puede tener más de 500 caracteres");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.fechaEnvio = LocalDateTime.now().format(formatter);
    }
}
