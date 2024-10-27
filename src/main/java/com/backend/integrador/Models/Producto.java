package com.backend.integrador.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "producto")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "image_url")
    private String imageUrl;  

    @Column(name = "precio", nullable = false)
    private double precio;

    @Column(name = "stock", nullable = false)
    private int stock;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    public Producto(Long id, String nombre, String imageUrl, double precio, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.imageUrl = imageUrl;
        this.precio = precio;
        this.stock = stock;
    }

    public Producto() {
    }
}
