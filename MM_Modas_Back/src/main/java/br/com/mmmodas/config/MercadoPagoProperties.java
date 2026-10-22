package br.com.mmmodas.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuracao de integracao com o gateway Mercado Pago.
 */
@ConfigurationProperties(prefix = "app.mercadopago")
public record MercadoPagoProperties(
        String accessToken,
        String webhookSecret,
        String notificationUrl
) {
}

