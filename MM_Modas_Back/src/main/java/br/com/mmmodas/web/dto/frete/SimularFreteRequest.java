package br.com.mmmodas.web.dto.frete;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record SimularFreteRequest(
        @NotNull(message = "Subtotal e obrigatorio")
        @DecimalMin(value = "0.00", message = "Subtotal nao pode ser negativo")
        BigDecimal subtotal,

        @NotBlank(message = "UF e obrigatoria")
        @Pattern(regexp = "^[A-Za-z]{2}$", message = "UF deve ter 2 letras")
        String uf
) {
}

