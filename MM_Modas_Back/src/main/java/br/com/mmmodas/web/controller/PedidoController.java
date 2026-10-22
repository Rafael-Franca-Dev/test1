package br.com.mmmodas.web.controller;

import br.com.mmmodas.domain.Pedido;
import br.com.mmmodas.security.UsuarioAutenticado;
import br.com.mmmodas.service.PedidoService;
import br.com.mmmodas.web.dto.pedido.PedidoRequest;
import br.com.mmmodas.web.dto.pedido.PedidoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponse> criar(@AuthenticationPrincipal UsuarioAutenticado usuario,
                                                @Valid @RequestBody PedidoRequest req) {
        try {
            Pedido pedido = pedidoService.criar(usuario.id(), req);
            return ResponseEntity.status(HttpStatus.CREATED).body(PedidoResponse.de(pedido));
        } catch (DataIntegrityViolationException ex) {
            if (req.idempotencyKey() != null) {
                return pedidoService.buscarPorIdempotencyKey(req.idempotencyKey().trim())
                        .map(p -> ResponseEntity.ok(PedidoResponse.de(p)))
                        .orElseThrow(() -> ex);
            }
            throw ex;
        }
    }

    @GetMapping
    public ResponseEntity<Page<PedidoResponse>> listarMeusPedidos(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<PedidoResponse> pagina = pedidoService.listarDoCliente(usuario.id(), pageable)
                .map(PedidoResponse::de);
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> buscarPorId(@AuthenticationPrincipal UsuarioAutenticado usuario,
                                                      @PathVariable UUID id) {
        Pedido pedido = pedidoService.buscarPorId(id);
        if (!pedido.getCliente().getId().equals(usuario.id()) && !usuario.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(PedidoResponse.de(pedido));
    }
}

