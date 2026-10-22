package br.com.mmmodas.web.dto.catalogo;

import br.com.mmmodas.domain.Produto;
import br.com.mmmodas.domain.ProdutoImagem;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProdutoResponse(
        UUID id,
        String nome,
        String slug,
        String descricao,
        String descricaoCurta,
        String categoriaSlug,
        String categoriaNome,
        String marca,
        BigDecimal precoBase,
        BigDecimal precoPromocional,
        BigDecimal precoEfetivo,
        boolean destaque,
        String imagemPrincipal,
        List<String> imagens,
        List<VariacaoResponse> variacoes
) {
    public static ProdutoResponse de(Produto p, List<VariacaoResponse> variacoes) {
        List<ProdutoImagem> imgsOrdenadas = p.getImagens().stream()
                .sorted((a, b) -> {
                    int cmp = Boolean.compare(b.isPrincipal(), a.isPrincipal());
                    return cmp != 0 ? cmp : Integer.compare(a.getOrdem(), b.getOrdem());
                })
                .toList();

        String imgPrincipal = imgsOrdenadas.isEmpty() ? null : imgsOrdenadas.get(0).getUrl();
        List<String> urls = imgsOrdenadas.stream().map(img -> img.getUrl()).toList();
        BigDecimal precoEfetivo = p.getPrecoPromocional() != null ? p.getPrecoPromocional() : p.getPrecoBase();

        return new ProdutoResponse(
                p.getId(),
                p.getNome(),
                p.getSlug(),
                p.getDescricao(),
                p.getDescricaoCurta(),
                p.getCategoria() != null ? p.getCategoria().getSlug() : null,
                p.getCategoria() != null ? p.getCategoria().getNome() : null,
                p.getMarca(),
                p.getPrecoBase(),
                p.getPrecoPromocional(),
                precoEfetivo,
                p.isDestaque(),
                imgPrincipal,
                urls,
                variacoes
        );
    }
}

