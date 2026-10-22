package br.com.mmmodas.web.dto.catalogo;

import br.com.mmmodas.domain.Categoria;

import java.util.UUID;

public record CategoriaResponse(
        UUID id,
        String nome,
        String slug,
        Integer ordem
) {
    public static CategoriaResponse de(Categoria c) {
        return new CategoriaResponse(c.getId(), c.getNome(), c.getSlug(), c.getOrdem());
    }
}

