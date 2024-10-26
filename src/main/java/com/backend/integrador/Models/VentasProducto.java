package com.backend.integrador.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "ventas_producto")
public class VentasProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    private int cantidad;
    
    @Column(name = "fecha_venta", nullable = false)
    private String fechaVenta;
}
