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

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)  
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "direccion_id", nullable = false)  
    private Direccion direccion;
}
