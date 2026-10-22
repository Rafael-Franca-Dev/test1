package br.com.mmmodas.web.dto.pedido;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/**
 * Checkout.
 *
 * idempotencyKey: o front deve gerar um UUID por tentativa de compra e reenviar
 * o MESMO valor se precisar repetir a chamada (timeout, duplo clique). O servidor
 * usa essa chave para devolver o pedido ja criado em vez de criar outro.
 * Sem isso, o classico "cliquei duas vezes e comprei duas vezes" acontece.
 *
 * A ausencia de valorFrete tambem e proposital: o frete e calculado no servidor.
 */
public record PedidoRequest(

        @NotNull(message = "enderecoId e obrigatorio")
        UUID enderecoId,

        @NotEmpty(message = "o pedido precisa de pelo menos um item")
        @Valid
        List<ItemPedidoRequest> itens,

        String cupomCodigo,

        @Size(max = 100, message = "idempotencyKey deve ter no maximo 100 caracteres")
        String idempotencyKey,

        @Size(max = 500)
        String observacoes
) {
}
