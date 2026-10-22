package br.com.mmmodas.web.dto.auth;

import br.com.mmmodas.web.dto.endereco.EnderecoRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @NotBlank(message = "Nome e obrigatorio")
        @Size(min = 3, max = 160, message = "Informe seu nome completo")
        String nome,

        @NotBlank(message = "E-mail e obrigatorio")
        @Email(message = "E-mail invalido")
        String email,

        @NotBlank(message = "Senha e obrigatoria")
        @Size(min = 6, max = 100, message = "A senha deve ter no minimo 6 caracteres")
        String senha,

        @Pattern(regexp = "^[0-9]{11}$", message = "CPF deve conter 11 digitos numericos")
        String cpf,

        @Size(max = 20)
        String telefone,

        @Valid
        EnderecoRequest endereco
) {
}

