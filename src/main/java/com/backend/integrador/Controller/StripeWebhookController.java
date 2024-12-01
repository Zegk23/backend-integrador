package com.backend.integrador.Controller;

import com.stripe.net.Webhook;
import io.github.cdimascio.dotenv.Dotenv;
import com.stripe.exception.SignatureVerificationException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stripe")
public class StripeWebhookController {

    private final String stripeWebhookSecret;

    public StripeWebhookController() {
        Dotenv dotenv = Dotenv.configure().load();
        this.stripeWebhookSecret = dotenv.get("STRIPE_WEBHOOK_SECRET");
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> handleStripeWebhook(@RequestBody String payload,
                                                 @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            // Validar la firma del webhook antes de procesar
            Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);

            // Lógica existente
            return ResponseEntity.ok("Evento procesado correctamente");
        } catch (SignatureVerificationException e) {
            // Firma inválida
            return ResponseEntity.status(400).body("Firma del webhook no válida: " + e.getMessage());
        } catch (Exception e) {
            // Otros errores
            return ResponseEntity.status(400).body("Error al procesar el webhook: " + e.getMessage());
        }
    }
}
