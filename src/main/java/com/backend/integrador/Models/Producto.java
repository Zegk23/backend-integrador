package com.backend.integrador.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "productos")
public class Producto {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "imgURL")
    private String imgURL;
    @Column(name = "precio")
    private double precio;
    @Column(name ="Stock")
    private int Stock;    

    public Producto(Long id, String nombre, String imgURL, double precio, int Stock){
        this.id = id;
        this.nombre = nombre;
        this.imgURL = imgURL;
        this.precio = precio;
        this.Stock = Stock;
    }
    public Producto(){
    }
}
