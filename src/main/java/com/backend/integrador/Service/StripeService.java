package com.backend.integrador.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import io.github.cdimascio.dotenv.Dotenv;

import org.springframework.stereotype.Service;

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
