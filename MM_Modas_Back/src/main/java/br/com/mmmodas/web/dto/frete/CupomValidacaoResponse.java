package br.com.mmmodas.web.dto.frete;

import br.com.mmmodas.domain.Cupom;
import br.com.mmmodas.domain.enums.TipoCupom;

import java.math.BigDecimal;

public record CupomValidacaoResponse(
        String codigo,
        TipoCupom tipo,
        BigDecimal valor,
        BigDecimal valorMinimo,
        boolean freteGratis
) {
    public static CupomValidacaoResponse de(Cupom c) {
        return new CupomValidacaoResponse(
                c.getCodigo(),
                c.getTipo(),
                c.getValor(),
                c.getValorMinimo(),
                c.isFreteGratis()
        );
    }
}

