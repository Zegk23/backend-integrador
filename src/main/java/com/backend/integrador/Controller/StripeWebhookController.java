package com.backend.integrador.Controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stripe")
public class StripeWebhookController {

    private static final String WEBHOOK_SECRET = "whsec_uftAF6OJBS6AyHPPz8kMrRmjd5GJJTBA"; // Tu clave secreta de webhook

    @PostMapping("/webhook")
    public ResponseEntity<?> handleStripeWebhook(@RequestBody String payload,
                                                 @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            // Verificar la firma del webhook
            Event event = Webhook.constructEvent(payload, sigHeader, WEBHOOK_SECRET);

            switch (event.getType()) {
                case "payment_intent.succeeded":
                    // Procesar un pago exitoso
                    System.out.println("Pago exitoso recibido.");
                    break;
                case "payment_intent.payment_failed":
                    // Procesar un pago fallido
                    System.out.println("El pago falló.");
                    break;
                default:
                    System.out.println("Evento no manejado: " + event.getType());
            }

            return ResponseEntity.ok("Evento procesado correctamente");
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(400).body("Firma del webhook no válida: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al procesar el webhook: " + e.getMessage());
        }
    }
}
