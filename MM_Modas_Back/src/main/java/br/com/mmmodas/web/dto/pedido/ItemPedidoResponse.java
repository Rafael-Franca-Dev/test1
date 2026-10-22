package br.com.mmmodas.web.dto.pedido;

import br.com.mmmodas.domain.PedidoItem;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemPedidoResponse(
        UUID id,
        UUID variacaoId,
        String produtoNome,
        String sku,
        String tamanho,
        String cor,
        int quantidade,
        BigDecimal precoUnitario,
        BigDecimal totalItem
) {
    public static ItemPedidoResponse de(PedidoItem i) {
        BigDecimal total = i.getTotalItem() != null
                ? i.getTotalItem()
                : i.getPrecoUnitario().multiply(BigDecimal.valueOf(i.getQuantidade()));
        return new ItemPedidoResponse(
                i.getId(),
                i.getVariacao().getId(),
                i.getProdutoNome(),
                i.getSku(),
                i.getTamanho(),
                i.getCor(),
                i.getQuantidade(),
                i.getPrecoUnitario(),
                total
        );
    }
}

