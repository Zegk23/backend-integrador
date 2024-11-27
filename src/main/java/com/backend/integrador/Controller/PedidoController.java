package com.backend.integrador.Controller;

import com.backend.integrador.Models.Pedido;
import com.backend.integrador.Models.PedidoProducto;
import com.backend.integrador.Models.RecojoTienda;
import com.backend.integrador.Service.PedidoService;
import com.backend.integrador.Service.StripeService;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import java.util.List;
import java.util.Map;

import com.backend.integrador.DTO.PedidoRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
            Pedido nuevoPedido = pedidoService.crearPedido(pedido, pedido.getPedidoProductos(), null); // Aquí se pasa
                                                                                                       // null para
                                                                                                       // RecojoTienda

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
    public ResponseEntity<?> crearPedido(@RequestBody PedidoRequest pedidoRequest) {
        try {
            Pedido pedido = pedidoRequest.getPedido();
            List<PedidoProducto> productos = pedidoRequest.getProductos();
            RecojoTienda recojoTienda = pedidoRequest.getRecojoTienda();

            Pedido nuevoPedido = pedidoService.crearPedido(pedido, productos, recojoTienda);
            return ResponseEntity.ok(nuevoPedido);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
