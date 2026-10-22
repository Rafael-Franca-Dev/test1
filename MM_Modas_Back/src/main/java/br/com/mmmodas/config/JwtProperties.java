package br.com.mmmodas.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.StandardCharsets;

/**
 * Configuracao de JWT. Valida no arranque em vez de falhar na primeira
 * requisicao: um segredo curto demais so quebraria quando alguem tentasse logar,
 * e um segredo fraco e falha de seguranca que passa despercebida.
 */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(String secret, long accessTokenMinutos, long refreshTokenDias) {

    public JwtProperties {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "app.jwt.secret nao configurado. Defina a variavel de ambiente JWT_SECRET.");
        }
        // HS256 exige chave de pelo menos 256 bits = 32 bytes.
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                    "app.jwt.secret precisa ter ao menos 32 bytes para HS256 (atual: "
                            + secret.getBytes(StandardCharsets.UTF_8).length + ").");
        }
        if (accessTokenMinutos <= 0) {
            throw new IllegalStateException("app.jwt.access-token-minutos deve ser maior que zero.");
        }
        if (refreshTokenDias <= 0) {
            throw new IllegalStateException("app.jwt.refresh-token-dias deve ser maior que zero.");
        }
    }

    public long accessTokenSegundos() {
        return accessTokenMinutos * 60;
    }
}
