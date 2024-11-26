package com.backend.integrador.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "pedido")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha", nullable = false)
    private String fecha;

    @ManyToOne
    @JoinColumn(name = "direccion_id", nullable = true)
    private Direccion direccion;

    @Column(name = "estado", nullable = false)
    private String estado = "Pendiente";

    @Column(name = "stripe_session_id", unique = true)
    private String stripeSessionId;

    // Inicialización de la lista pedidoProductos
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoProducto> pedidoProductos = new ArrayList<>(); // Se inicializa aquí

    // Método para sincronizar la relación bidireccional
    public void agregarPedidoProducto(PedidoProducto pedidoProducto) {
        pedidoProductos.add(pedidoProducto); // Esto ya no lanzará NullPointerException
        pedidoProducto.setPedido(this);
    }
}
