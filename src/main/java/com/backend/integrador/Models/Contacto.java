package com.backend.integrador.Models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    private String fechaEnvio;  // Fecha como VARCHAR
    
    @PrePersist
    protected void onCreate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.fechaEnvio = LocalDateTime.now().format(formatter);
    }
}
