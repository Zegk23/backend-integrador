package com.backend.integrador.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "pago")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Column(name = "monto", nullable = false)
    private double monto;

    @Column(name = "fecha", nullable = false)
    private String fecha;

    @Column(name = "stripe_payment_id")
    private String stripePaymentId;

    @Column(name = "stripe_session_id")
    private String stripeSessionId;

    @Column(name = "estado", nullable = false)
    private String estado = "Pendiente";

    @ManyToOne
    @JoinColumn(name = "metodo_pago_id", nullable = false)
    private MetodoPago metodoPago;
}
