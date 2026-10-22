package br.com.mmmodas.web.dto.frete;

import java.math.BigDecimal;

public record FreteResponse(
        BigDecimal valorFrete,
        BigDecimal limiteFreteGratis,
        boolean freteGratis,
        String uf
) {
}

