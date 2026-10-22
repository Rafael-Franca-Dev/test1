package br.com.mmmodas.web.dto.auth;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        UsuarioResponse usuario
) {
}

