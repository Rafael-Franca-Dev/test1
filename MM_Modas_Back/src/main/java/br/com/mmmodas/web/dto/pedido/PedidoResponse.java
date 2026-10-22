package br.com.mmmodas.web.dto.pedido;

import br.com.mmmodas.domain.Pedido;
import br.com.mmmodas.domain.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record PedidoResponse(
        UUID id,
        String numero,
        StatusPedido status,
        BigDecimal subtotal,
        BigDecimal desconto,
        BigDecimal valorFrete,
        BigDecimal total,
        String entregaDestinatario,
        String entregaCep,
        String entregaLogradouro,
        String entregaNumero,
        String entregaComplemento,
        String entregaBairro,
        String entregaCidade,
        String entregaUf,
        OffsetDateTime criadoEm,
        List<ItemPedidoResponse> itens
) {
    public static PedidoResponse de(Pedido p) {
        List<ItemPedidoResponse> itens = p.getItens().stream()
                .map(ItemPedidoResponse::de)
                .toList();
        return new PedidoResponse(
                p.getId(),
                p.getNumero(),
                p.getStatus(),
                p.getSubtotal(),
                p.getDesconto(),
                p.getValorFrete(),
                p.getTotal(),
                p.getEntregaDestinatario(),
                p.getEntregaCep(),
                p.getEntregaLogradouro(),
                p.getEntregaNumero(),
                p.getEntregaComplemento(),
                p.getEntregaBairro(),
                p.getEntregaCidade(),
                p.getEntregaUf(),
                p.getCriadoEm(),
                itens
        );
    }
}

