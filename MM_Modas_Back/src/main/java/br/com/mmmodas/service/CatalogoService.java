package br.com.mmmodas.service;

import br.com.mmmodas.domain.Produto;
import br.com.mmmodas.domain.Variacao;
import br.com.mmmodas.exception.RecursoNaoEncontradoException;
import br.com.mmmodas.repository.CategoriaRepository;
import br.com.mmmodas.repository.EstoqueRepository;
import br.com.mmmodas.repository.ProdutoRepository;
import br.com.mmmodas.repository.VariacaoRepository;
import br.com.mmmodas.web.dto.catalogo.CategoriaResponse;
import br.com.mmmodas.web.dto.catalogo.ProdutoResponse;
import br.com.mmmodas.web.dto.catalogo.VariacaoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogoService {

    private final CategoriaRepository categoriaRepository;
    private final ProdutoRepository produtoRepository;
    private final VariacaoRepository variacaoRepository;
    private final EstoqueRepository estoqueRepository;

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarCategorias() {
        return categoriaRepository.findByAtivaTrueOrderByOrdemAscNomeAsc()
                .stream()
                .map(CategoriaResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public List<ProdutoResponse> listarProdutos() {
        List<Produto> produtos = produtoRepository.listarAtivosComCategoriaEImagens();
        if (produtos.isEmpty()) {
            return List.of();
        }

        List<UUID> produtoIds = produtos.stream().map(p -> p.getId()).toList();
        List<Variacao> variacoes = variacaoRepository.buscarAtivasPorProdutos(produtoIds);

        List<UUID> variacaoIds = variacoes.stream().map(v -> v.getId()).toList();
        Map<UUID, Integer> saldoPorVariacao = estoqueRepository.findAllById(variacaoIds)
                .stream()
                .collect(Collectors.toMap(e -> e.getVariacaoId(), e -> e.disponivel()));

        Map<UUID, List<VariacaoResponse>> variacoesPorProduto = new HashMap<>();
        for (Variacao v : variacoes) {
            int disponivel = saldoPorVariacao.getOrDefault(v.getId(), 0);
            variacoesPorProduto
                    .computeIfAbsent(v.getProduto().getId(), k -> new ArrayList<>())
                    .add(VariacaoResponse.de(v, disponivel));
        }

        return produtos.stream()
                .map(p -> ProdutoResponse.de(p, variacoesPorProduto.getOrDefault(p.getId(), List.of())))
                .toList();
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public ProdutoResponse buscarPorSlug(String slug) {
        Produto produto = produtoRepository.buscarAtivoPorSlugComDetalhes(slug)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto nao encontrado: " + slug));

        List<Variacao> variacoes = variacaoRepository.buscarAtivasPorProdutos(List.of(produto.getId()));
        List<UUID> variacaoIds = variacoes.stream().map(v -> v.getId()).toList();
        Map<UUID, Integer> saldoPorVariacao = estoqueRepository.findAllById(variacaoIds)
                .stream()
                .collect(Collectors.toMap(e -> e.getVariacaoId(), e -> e.disponivel()));

        List<VariacaoResponse> variacoesResp = variacoes.stream()
                .map(v -> VariacaoResponse.de(v, saldoPorVariacao.getOrDefault(v.getId(), 0)))
                .toList();

        return ProdutoResponse.de(produto, variacoesResp);
    }
}

