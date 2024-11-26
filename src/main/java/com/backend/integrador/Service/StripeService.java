package com.backend.integrador.Service;

import com.backend.integrador.Models.Pedido;
import com.backend.integrador.Models.PedidoProducto;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import io.github.cdimascio.dotenv.Dotenv;
import com.backend.integrador.Repository.PedidoRepositorio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StripeService {

    @Autowired
    PedidoRepositorio pedidoRepositorio;

    private final String stripeSecretKey;

    public StripeService() {
        // Cargar las claves desde el archivo .env
        Dotenv dotenv = Dotenv.configure().load();
        this.stripeSecretKey = dotenv.get("STRIPE_SECRET_KEY");
    }

    public PaymentIntent crearPaymentIntent(PaymentIntentCreateParams params) throws StripeException {
        Stripe.apiKey = stripeSecretKey;
        return PaymentIntent.create(params);
    }

    public String crearSesionDeStripe(Pedido pedido) throws Exception {
        Stripe.apiKey = stripeSecretKey;

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
        for (PedidoProducto pp : pedido.getPedidoProductos()) {
            lineItems.add(
                    SessionCreateParams.LineItem.builder()
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("pen")
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData
                                                            .builder()
                                                            .setName(pp.getProducto().getNombre())
                                                            .build())
                                            .setUnitAmount((long) (pp.getProducto().getPrecio() * 100))
                                            .build())
                            .setQuantity((long) pp.getCantidad())
                            .build());
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addAllLineItem(lineItems)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:3000/cancel")
                .build();

        Session session = Session.create(params);

        pedido.setStripeSessionId(session.getId());
        pedidoRepositorio.save(pedido);

        return session.getId();
    }
}
