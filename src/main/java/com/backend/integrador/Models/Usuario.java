package com.backend.integrador.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellido", nullable = false)
    private String apellido;

    @Column(name = "correo", nullable = false, unique = true)
    private String correo;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "rol_id", nullable = false)  // Relación ManyToOne con Rol
    private Rol rol;

    @OneToMany(mappedBy = "usuario")  // Un Usuario tiene muchas Direcciones
    private List<Direccion> direcciones;

    @OneToMany(mappedBy = "usuario")  // Un Usuario puede tener muchos Pedidos
    private List<Pedido> pedidos;
}
