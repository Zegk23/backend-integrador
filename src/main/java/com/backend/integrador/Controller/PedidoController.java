package com.backend.integrador.Controller;

import com.backend.integrador.DTO.PedidoRequest;
import com.backend.integrador.Models.Pedido;
import com.backend.integrador.Models.PedidoProducto;
import com.backend.integrador.Models.RecojoTienda;
import com.backend.integrador.Service.PedidoService;
import com.backend.integrador.Service.StripeService;
import com.backend.integrador.Util.JWTUtil;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private StripeService stripeService;

    @PostMapping("/crear-payment-intent")
    public ResponseEntity<?> crearPaymentIntent(@RequestBody PedidoRequest pedidoRequest) {
        try {
            log.info("PedidoRequest recibido para crear PaymentIntent: {}", pedidoRequest);

            Pedido pedido = pedidoRequest.getPedido();
            List<PedidoProducto> productos = pedidoRequest.getProductos();
            RecojoTienda recojoTienda = pedidoRequest.getRecojoTienda();

            Pedido nuevoPedido = pedidoService.crearPedido(
                    pedido,
                    productos,
                    recojoTienda,
                    "stripe_payment_id_mock",
                    "Tarjeta de débito");

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((long) (nuevoPedido.calcularTotal() * 100))
                    .setCurrency("pen")
                    .setDescription("Compra en Mi Tienda")
                    .build();

            PaymentIntent paymentIntent = stripeService.crearPaymentIntent(params);

            return ResponseEntity.ok(Map.of("clientSecret", paymentIntent.getClientSecret()));
        } catch (Exception e) {
            log.error("Error al crear el PaymentIntent: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el PaymentIntent: " + e.getMessage());
        }
    }

    @PostMapping("/crear-pedido")
    public ResponseEntity<?> crearPedido(@RequestHeader("Authorization") String token,
                                         @RequestBody PedidoRequest pedidoRequest) {
        try {
            // Validar si el token está presente
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token no proporcionado o inválido.");
            }

            String jwtToken = token.substring(7); // Remover "Bearer " del token
            if (!jwtUtil.validarToken(jwtToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado.");
            }

            Long userIdFromToken = jwtUtil.getUserId(jwtToken);

            Pedido pedido = pedidoRequest.getPedido();

            // Validar que el usuario del pedido coincida con el usuario autenticado
            if (pedido.getUsuario() == null || !pedido.getUsuario().getId().equals(userIdFromToken)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("El usuario del pedido no coincide con el usuario autenticado.");
            }

            Pedido nuevoPedido = pedidoService.crearPedido(
                    pedido,
                    pedidoRequest.getProductos(),
                    pedidoRequest.getRecojoTienda(),
                    "stripe_payment_id_mock",
                    "Tarjeta de débito");

            return ResponseEntity.ok(nuevoPedido);
        } catch (Exception e) {
            log.error("Error al crear el pedido: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar el pedido.");
        }
    }
}
