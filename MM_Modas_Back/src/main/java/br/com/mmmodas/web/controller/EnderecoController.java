package br.com.mmmodas.web.controller;

import br.com.mmmodas.security.UsuarioAutenticado;
import br.com.mmmodas.service.EnderecoService;
import br.com.mmmodas.web.dto.endereco.EnderecoRequest;
import br.com.mmmodas.web.dto.endereco.EnderecoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enderecos")
@RequiredArgsConstructor
public class EnderecoController {

    private final EnderecoService enderecoService;

    @GetMapping
    public ResponseEntity<List<EnderecoResponse>> listar(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(enderecoService.listarDoCliente(usuario.id()));
    }

    @PostMapping
    public ResponseEntity<EnderecoResponse> criar(@AuthenticationPrincipal UsuarioAutenticado usuario,
                                                  @Valid @RequestBody EnderecoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enderecoService.criar(usuario.id(), req));
    }
}

