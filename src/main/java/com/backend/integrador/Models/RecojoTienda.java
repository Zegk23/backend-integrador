package com.backend.integrador.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "recojo_tienda")
public class RecojoTienda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    @JsonIgnore
    private Pedido pedido;

    @Column(name = "local", nullable = false)
    private String local;

    @Column(name = "horario", nullable = false)
    private String horario;

    @Column(name = "receptor_nombre", nullable = false)
    private String receptorNombre;

    @Column(name = "receptor_dni", nullable = false)
    private String receptorDni;
}
