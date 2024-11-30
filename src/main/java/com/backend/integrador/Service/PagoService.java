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

    public Pago registrarPago(Pedido pedido, double monto, String fecha, String stripePaymentId, String metodoPagoNombre) {
        log.info("Iniciando registro de pago para el pedido ID: {}", pedido.getId());

        MetodoPago metodoPago = metodoPagoRepositorio.findByNombre(metodoPagoNombre)
                .orElseGet(() -> {
                    log.info("Método de pago '{}' no encontrado. Creando uno nuevo.", metodoPagoNombre);
                    MetodoPago nuevoMetodoPago = new MetodoPago();
                    nuevoMetodoPago.setNombre(metodoPagoNombre);
                    nuevoMetodoPago.setDescripcion("Pago realizado con " + metodoPagoNombre);
                    nuevoMetodoPago.setProveedor("Stripe");
                    return metodoPagoRepositorio.save(nuevoMetodoPago);
                });

        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setFecha(fecha);
        pago.setStripePaymentId(stripePaymentId);
        pago.setEstado("Completado");
        pago.setMetodoPago(metodoPago);

        Pago pagoGuardado = pagoRepositorio.save(pago);
        log.info("Pago registrado con éxito: {}", pagoGuardado);
        return pagoGuardado;
    }
}
