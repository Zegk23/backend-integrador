package com.backend.integrador.Controller;

import com.backend.integrador.Models.Pedido;
import com.backend.integrador.Repository.PedidoRepositorio;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stripe")
public class StripeWebhookController {

    @PostMapping("/webhook")
    public ResponseEntity<?> handleStripeWebhook(@RequestBody String payload,
                                                 @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            // Manejo general de eventos de Stripe, si corresponde
            return ResponseEntity.ok("Evento procesado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error al procesar el webhook: " + e.getMessage());
        }
    }
}

    

