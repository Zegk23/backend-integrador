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

        // Imprimir la clave para verificar (solo para debugging, eliminar en producción)
        System.out.println("Stripe Secret Key: " + this.stripeSecretKey);
    }

    public String crearSesionDeStripe(Pedido pedido) throws Exception {
        // Configurar Stripe con la clave secreta
        Stripe.apiKey = stripeSecretKey;

        // Crear los line items basados en los productos del pedido
        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
        for (PedidoProducto pp : pedido.getPedidoProductos()) {
            // Validar que los datos del producto estén completos
            if (pp.getProducto() == null || pp.getProducto().getNombre() == null || pp.getProducto().getPrecio() == 0) {
                throw new IllegalArgumentException("El producto en PedidoProducto no tiene información válida.");
            }

            // Logs para verificar los datos del producto
            System.out.println("Preparando LineItem:");
            System.out.println("Nombre del producto: " + pp.getProducto().getNombre());
            System.out.println("Precio del producto (en centavos): " + (long) (pp.getProducto().getPrecio() * 100));
            System.out.println("Cantidad: " + pp.getCantidad());

            // Crear el LineItem para Stripe
            lineItems.add(
                SessionCreateParams.LineItem.builder()
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("pen") // Moneda: Soles Peruanos
                            .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName(pp.getProducto().getNombre()) // Nombre del producto
                                    .build())
                            .setUnitAmount((long) (pp.getProducto().getPrecio() * 100)) // Convertir a centavos
                            .build())
                    .setQuantity((long) pp.getCantidad()) // Cantidad del producto
                    .build());
        }

        // Log de los LineItems preparados
        System.out.println("Total LineItems preparados: " + lineItems.size());

        // Crear la sesión de Stripe
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD) // Método de pago: Tarjeta
                .addAllLineItem(lineItems) // Productos del pedido
                .setMode(SessionCreateParams.Mode.PAYMENT) // Modo de pago
                .setSuccessUrl("http://localhost:3000/success?session_id={CHECKOUT_SESSION_ID}") // URL de éxito
                .setCancelUrl("http://localhost:3000/cancel") // URL de cancelación
                .build();

        // Crear la sesión en Stripe y devolver el sessionId
        Session session = Session.create(params);
        System.out.println("Stripe Session creada con ID: " + session.getId());

        return session.getId(); // Devolver el sessionId
    }
}
