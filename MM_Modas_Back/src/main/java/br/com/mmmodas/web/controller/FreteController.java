package br.com.mmmodas.web.controller;

import br.com.mmmodas.service.FreteService;
import br.com.mmmodas.web.dto.frete.FreteResponse;
import br.com.mmmodas.web.dto.frete.SimularFreteRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/frete")
@RequiredArgsConstructor
public class FreteController {

    private final FreteService freteService;

    @PostMapping("/simular")
    public ResponseEntity<FreteResponse> simular(@Valid @RequestBody SimularFreteRequest req) {
        BigDecimal valor = freteService.calcular(req.subtotal(), req.uf());
        boolean gratis = valor.compareTo(BigDecimal.ZERO) == 0;
        return ResponseEntity.ok(new FreteResponse(
                valor,
                freteService.limiteFreteGratis(),
                gratis,
                req.uf().trim().toUpperCase()
        ));
    }
}

