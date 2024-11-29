package com.backend.integrador.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<PedidoProducto> pedidoProductos = new ArrayList<>();

    public void agregarPedidoProducto(PedidoProducto pedidoProducto) {
        pedidoProductos.add(pedidoProducto);
        pedidoProducto.setPedido(this);
    }

    public double calcularTotal() {
        return this.getPedidoProductos().stream()
                .mapToDouble(pp -> pp.getProducto().getPrecio() * pp.getCantidad())
                .sum();
    }
}
