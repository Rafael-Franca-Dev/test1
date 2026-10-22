package br.com.mmmodas.web.dto.endereco;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EnderecoRequest(
        String apelido,

        @NotBlank(message = "Destinatario e obrigatorio")
        String destinatario,

        @NotBlank(message = "CEP e obrigatorio")
        @Pattern(regexp = "^[0-9]{8}$", message = "CEP deve ter 8 digitos numericos")
        String cep,

        @NotBlank(message = "Logradouro e obrigatorio")
        String logradouro,

        @NotBlank(message = "Numero e obrigatorio")
        @Size(max = 20)
        String numero,

        String complemento,

        @NotBlank(message = "Bairro e obrigatorio")
        String bairro,

        @NotBlank(message = "Cidade e obrigatoria")
        String cidade,

        @NotBlank(message = "UF e obrigatoria")
        @Pattern(regexp = "^[A-Za-z]{2}$", message = "UF deve ter 2 letras")
        String uf,

        Boolean principal
) {
}

