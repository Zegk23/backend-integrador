package com.backend.integrador.Controller;

import com.backend.integrador.Models.Pedido;
import com.backend.integrador.Service.PedidoService;
import com.backend.integrador.Service.StripeService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private StripeService stripeService;

    /**
     * Endpoint para crear un pedido y generar una sesión de pago con Stripe
     */
    @PostMapping("/crear-pedido")
    public ResponseEntity<?> crearPedidoConStripe(@RequestBody Pedido pedido) {
        try {
            // Crear el pedido
            Pedido nuevoPedido = pedidoService.crearPedido(pedido, pedido.getPedidoProductos());

            // Generar la sesión de Stripe
            String sessionId = stripeService.crearSesionDeStripe(nuevoPedido);

            if (sessionId == null || sessionId.isEmpty()) {
                throw new RuntimeException("No se pudo generar el sessionId de Stripe");
            }

            // Enviar el sessionId al frontend
            return ResponseEntity.ok(Map.of("sessionId", sessionId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // Log completo para debug
            return ResponseEntity.status(500).body("Error en el proceso: " + e.getMessage());
        }
    }

}
