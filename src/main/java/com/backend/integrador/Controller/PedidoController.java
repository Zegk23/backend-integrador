package com.backend.integrador.Controller;

import com.backend.integrador.Models.Pedido;
import com.backend.integrador.Service.PedidoService;
import com.backend.integrador.Service.StripeService;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

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

    @PostMapping("/crear-payment-intent")
    public ResponseEntity<?> crearPaymentIntent(@RequestBody Pedido pedido) {
        try {
            // Crear el pedido en la base de datos (si corresponde)
            Pedido nuevoPedido = pedidoService.crearPedido(pedido, pedido.getPedidoProductos());

            // Configurar los parámetros del PaymentIntent
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((long) (nuevoPedido.calcularTotal() * 100)) // Total en centavos
                    .setCurrency("pen") // Moneda
                    .setDescription("Compra en Mi Tienda")
                    .build();

            // Crear el PaymentIntent en Stripe
            PaymentIntent paymentIntent = stripeService.crearPaymentIntent(params);

            // Retornar el clientSecret al frontend
            return ResponseEntity.ok(Map.of("clientSecret", paymentIntent.getClientSecret()));
        } catch (Exception e) {
            // Manejar errores y devolver un mensaje
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al crear el Payment Intent: " + e.getMessage());
        }
    }

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
