package br.com.mmmodas.web.dto.auth;

import br.com.mmmodas.domain.Cliente;
import br.com.mmmodas.domain.enums.PerfilUsuario;
import br.com.mmmodas.web.dto.endereco.EnderecoResponse;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UsuarioResponse(
        UUID id,
        String nome,
        String email,
        String cpf,
        String telefone,
        PerfilUsuario perfil,
        OffsetDateTime criadoEm,
        EnderecoResponse enderecoPrincipal
) {
    public static UsuarioResponse de(Cliente c, EnderecoResponse enderecoPrincipal) {
        return new UsuarioResponse(
                c.getId(),
                c.getNome(),
                c.getEmail(),
                c.getCpf(),
                c.getTelefone(),
                c.getPerfil(),
                c.getCriadoEm(),
                enderecoPrincipal
        );
    }
}

