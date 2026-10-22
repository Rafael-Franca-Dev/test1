package br.com.mmmodas.web.controller;

import br.com.mmmodas.service.CatalogoService;
import br.com.mmmodas.web.dto.catalogo.CategoriaResponse;
import br.com.mmmodas.web.dto.catalogo.ProdutoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CatalogoController {

    private final CatalogoService catalogoService;

    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaResponse>> listarCategorias() {
        return ResponseEntity.ok(catalogoService.listarCategorias());
    }

    @GetMapping("/produtos")
    public ResponseEntity<List<ProdutoResponse>> listarProdutos() {
        return ResponseEntity.ok(catalogoService.listarProdutos());
    }

    @GetMapping("/produtos/{slug}")
    public ResponseEntity<ProdutoResponse> buscarPorSlug(@PathVariable String slug) {
        return ResponseEntity.ok(catalogoService.buscarPorSlug(slug));
    }
}

