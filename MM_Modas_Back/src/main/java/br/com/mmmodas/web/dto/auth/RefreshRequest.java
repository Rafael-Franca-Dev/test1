package br.com.mmmodas.web.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @NotBlank(message = "refreshToken e obrigatorio")
        String refreshToken
) {
}

