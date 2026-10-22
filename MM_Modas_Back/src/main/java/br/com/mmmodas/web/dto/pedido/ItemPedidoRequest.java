package br.com.mmmodas.web.dto.pedido;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Item do pedido conforme enviado pelo front.
 *
 * Nota: nao existe campo de preco aqui, e isso e proposital. O preco e lido do
 * banco no momento do checkout. Aceitar o preco do front permitiria comprar uma
 * peca de R$ 300 por R$ 3 alterando o JSON.
 */
public record ItemPedidoRequest(

        @NotNull(message = "variacaoId e obrigatorio")
        UUID variacaoId,

        @Min(value = 1, message = "quantidade minima e 1")
        int quantidade
) {
}
