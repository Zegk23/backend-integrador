package com.backend.integrador.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "confirmacionpedido")
public class ConfirmacionPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Column(name = "correo_usuario", nullable = false)
    private String correoUsuario;

    @Column(name = "fecha_envio", nullable = false)
    private String fechaEnvio;  

    @Column(name = "estado", nullable = false)
    private String estado;
}
