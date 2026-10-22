package br.com.mmmodas.security;

import br.com.mmmodas.domain.enums.PerfilUsuario;

import java.util.UUID;

/**
 * Quem esta autenticado nesta requisicao. Fica disponivel via
 * @AuthenticationPrincipal nos controllers - os controllers nunca leem o
 * cliente_id do corpo da requisicao, senao qualquer pessoa poderia agir em nome
 * de outra trocando um UUID no JSON.
 */
public record UsuarioAutenticado(UUID id, String email, String nome, PerfilUsuario perfil) {

    public boolean isAdmin() {
        return perfil != null && perfil.isAdmin();
    }
}
