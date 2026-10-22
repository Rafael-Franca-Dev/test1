package br.com.mmmodas.web.dto.catalogo;

import br.com.mmmodas.domain.Variacao;

import java.math.BigDecimal;
import java.util.UUID;

public record VariacaoResponse(
        UUID id,
        String sku,
        String tamanho,
        String cor,
        BigDecimal preco,
        int disponivel,
        boolean emEstoque
) {
    public static VariacaoResponse de(Variacao v, int disponivel) {
        return new VariacaoResponse(
                v.getId(),
                v.getSku(),
                v.getTamanho(),
                v.getCor(),
                v.precoEfetivo(),
                disponivel,
                disponivel > 0
        );
    }
}

