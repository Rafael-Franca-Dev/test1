package br.com.mmmodas.web.controller;

import br.com.mmmodas.domain.Cupom;
import br.com.mmmodas.exception.RecursoNaoEncontradoException;
import br.com.mmmodas.exception.RegraDeNegocioException;
import br.com.mmmodas.repository.CupomRepository;
import br.com.mmmodas.web.dto.frete.CupomValidacaoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/cupons")
@RequiredArgsConstructor
public class CupomController {

    private final CupomRepository cupomRepository;

    @GetMapping("/validar/{codigo}")
    public ResponseEntity<CupomValidacaoResponse> validar(@PathVariable String codigo) {
        Cupom cupom = cupomRepository.findByCodigoIgnoreCase(codigo.trim())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cupom nao encontrado: " + codigo));

        if (!cupom.podeSerUsado(cupom.getValorMinimo().max(BigDecimal.ONE), OffsetDateTime.now())) {
            throw new RegraDeNegocioException("CUPOM_INDISPONIVEL", "Cupom expirado ou esgotado.");
        }

        return ResponseEntity.ok(CupomValidacaoResponse.de(cupom));
    }
}

