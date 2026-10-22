package br.com.mmmodas.web.dto.endereco;

import br.com.mmmodas.domain.Endereco;

import java.util.UUID;

public record EnderecoResponse(
        UUID id,
        String apelido,
        String destinatario,
        String cep,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String uf,
        boolean principal
) {
    public static EnderecoResponse de(Endereco e) {
        return new EnderecoResponse(
                e.getId(),
                e.getApelido(),
                e.getDestinatario(),
                e.getCep(),
                e.getLogradouro(),
                e.getNumero(),
                e.getComplemento(),
                e.getBairro(),
                e.getCidade(),
                e.getUf(),
                e.isPrincipal()
        );
    }
}

