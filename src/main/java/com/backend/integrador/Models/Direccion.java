package com.backend.integrador.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "direccion")
public class Direccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "direccion", nullable = false)
    private String direccion;

    @Column(name = "ciudad", nullable = false)
    private String ciudad;

    @Column(name = "codigo_postal", nullable = false)
    private String codigoPostal;

    @Column(name = "pais", nullable = false)
    private String pais;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)  // Relación ManyToOne con Usuario
    private Usuario usuario;
}
