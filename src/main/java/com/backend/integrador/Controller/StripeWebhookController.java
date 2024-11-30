package com.backend.integrador.Controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stripe")
public class StripeWebhookController {

    @PostMapping("/webhook")
    public ResponseEntity<?> handleStripeWebhook(@RequestBody String payload,
                                                 @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            return ResponseEntity.ok("Evento procesado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error al procesar el webhook: " + e.getMessage());
        }
    }
}

    

