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

    private final String stripeSecretKey;

    public StripeService() {
        Dotenv dotenv = Dotenv.configure().load();
        this.stripeSecretKey = dotenv.get("STRIPE_SECRET_KEY");
    }

    public PaymentIntent crearPaymentIntent(PaymentIntentCreateParams params) throws StripeException {
        Stripe.apiKey = stripeSecretKey;
        return PaymentIntent.create(params);
    }
}
