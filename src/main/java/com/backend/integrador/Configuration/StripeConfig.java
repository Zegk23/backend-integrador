package com.backend.integrador.Configuration;

import io.github.cdimascio.dotenv.Dotenv;

public class StripeConfig {

    private static final Dotenv dotenv = Dotenv.load();

    public static void main(String[] args) {
        String stripePublicKey = dotenv.get("STRIPE_PUBLIC_KEY");
        String stripeSecretKey = dotenv.get("STRIPE_SECRET_KEY");

        System.out.println("Clave pública de Stripe: " + stripePublicKey);
        System.out.println("Clave secreta de Stripe: " + stripeSecretKey);
    }
}
