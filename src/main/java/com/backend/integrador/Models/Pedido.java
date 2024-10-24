package com.backend.integrador.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "pedido")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha", nullable = false)
    private String fecha;  

    @Column(name = "total", nullable = false)
    private double total;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)  // Relación con Usuario
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "direccion_id", nullable = false)  // Relación con Direccion
    private Direccion direccion;
}
