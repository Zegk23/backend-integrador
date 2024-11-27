package com.backend.integrador.Service;

import com.backend.integrador.Models.MetodoPago;
import com.backend.integrador.Models.Pago;
import com.backend.integrador.Models.Pedido;
import com.backend.integrador.Repository.MetodoPagoRepositorio;
import com.backend.integrador.Repository.PagoRepositorio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PagoService {

    @Autowired
    private PagoRepositorio pagoRepositorio;

    @Autowired
    private MetodoPagoRepositorio metodoPagoRepositorio;

    /**
     * Registra un pago en la base de datos.
     *
     * @param pedido            El pedido relacionado con el pago.
     * @param monto             El monto total del pago.
     * @param fecha             La fecha del pago.
     * @param stripePaymentId   El ID del pago generado por Stripe.
     * @param stripeSessionId   El ID de la sesión de Stripe (opcional).
     * @param metodoPagoNombre  El nombre del método de pago (por ejemplo, "Tarjeta de crédito").
     * @return El objeto Pago registrado.
     */
    public Pago registrarPago(Pedido pedido, double monto, String fecha, String stripePaymentId, String stripeSessionId, String metodoPagoNombre) {
        log.info("Iniciando registro de pago para el pedido ID: {}", pedido.getId());
    
        // Buscar o crear el método de pago
        MetodoPago metodoPago = metodoPagoRepositorio.findByNombre(metodoPagoNombre)
                .orElseGet(() -> {
                    log.info("Método de pago '{}' no encontrado. Creando uno nuevo.", metodoPagoNombre);
                    MetodoPago nuevoMetodoPago = new MetodoPago();
                    nuevoMetodoPago.setNombre(metodoPagoNombre);
                    nuevoMetodoPago.setDescripcion("Pago realizado con " + metodoPagoNombre);
                    return metodoPagoRepositorio.save(nuevoMetodoPago);
                });
    
        // Crear y guardar el pago
        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setMonto(monto); // Asegúrate de que el monto sea el total correcto
        pago.setFecha(fecha);
        pago.setStripePaymentId(stripePaymentId);
        pago.setEstado("Completado");
        pago.setMetodoPago(metodoPago);
    
        Pago pagoGuardado = pagoRepositorio.save(pago);
        log.info("Pago registrado con éxito: {}", pagoGuardado);
        return pagoGuardado;
    }
    
}
