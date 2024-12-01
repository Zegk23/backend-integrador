package com.backend.integrador.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stripe")
public class StripeWebhookController {

    @PostMapping("/webhook")
    public ResponseEntity<?> handleStripeWebhook(@RequestBody String payload) {
        try {
            // Procesar el payload directamente
            System.out.println("Evento recibido: " + payload);
            return ResponseEntity.ok("Evento procesado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al procesar el webhook: " + e.getMessage());
        }
    }
}
