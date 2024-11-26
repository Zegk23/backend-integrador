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

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    // Secret del webhook obtenido de Stripe
    private static final String ENDPOINT_SECRET = System.getenv("STRIPE_WEBHOOK_SECRET");

    @PostMapping("/webhook")
    public ResponseEntity<?> handleStripeWebhook(@RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            // Cargar el secreto desde las variables de entorno
            String endpointSecret = System.getenv("STRIPE_WEBHOOK_SECRET");
            if (endpointSecret == null) {
                throw new IllegalStateException("Secreto del webhook no configurado");
            }

            // Validar la autenticidad del evento
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            // Manejar el evento específico
            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);

                if (session != null) {
                    // Busca el pedido relacionado al sessionId
                    Pedido pedido = pedidoRepositorio.findByStripeSessionId(session.getId());
                    if (pedido != null) {
                        // Actualiza el estado del pedido a "Pagado"
                        pedido.setEstado("Pagado");
                        pedidoRepositorio.save(pedido);
                    }
                }
            }

            return ResponseEntity.ok("Evento procesado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error al procesar el webhook: " + e.getMessage());
        }
    }

}
