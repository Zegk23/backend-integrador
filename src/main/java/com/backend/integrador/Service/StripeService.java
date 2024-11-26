package com.backend.integrador.Service;

import com.backend.integrador.Models.Pedido;
import com.backend.integrador.Models.PedidoProducto;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StripeService {

        private final String stripeSecretKey;

        public StripeService() {
                // Cargar las claves desde el archivo .env
                Dotenv dotenv = Dotenv.configure().load();
                this.stripeSecretKey = dotenv.get("STRIPE_SECRET_KEY");
        }

        public String crearSesionDeStripe(Pedido pedido) throws Exception {
                // Configurar Stripe con la clave secreta
                Stripe.apiKey = stripeSecretKey;
            
                // Crear los line items basados en los productos del pedido
                List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
                for (PedidoProducto pp : pedido.getPedidoProductos()) {
                    lineItems.add(
                        SessionCreateParams.LineItem.builder()
                            .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("pen") // Cambiar la moneda a Soles Peruanos
                                    .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName(pp.getProducto().getNombre()) // Nombre del producto
                                            .build())
                                    .setUnitAmount((long) (pp.getProducto().getPrecio() * 100)) // Stripe trabaja en centavos
                                    .build())
                            .setQuantity((long) pp.getCantidad()) // Cantidad del producto
                            .build());
                }
            
                // Crear la sesión de Stripe
                SessionCreateParams params = SessionCreateParams.builder()
                        .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD) // Especifica el método de pago
                        .addAllLineItem(lineItems) // Agrega los productos al pedido
                        .setMode(SessionCreateParams.Mode.PAYMENT) // Configura el modo de pago
                        .setSuccessUrl("http://localhost:3000/success?session_id={CHECKOUT_SESSION_ID}") // URL en caso de éxito
                        .setCancelUrl("http://localhost:3000/cancel") // URL en caso de cancelación
                        .build();
            
                Session session = Session.create(params);
            
                return session.getId(); // Devolver el sessionId
            }
            
}
