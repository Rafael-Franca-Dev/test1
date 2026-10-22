package br.com.mmmodas.service;

import br.com.mmmodas.domain.Estoque;
import br.com.mmmodas.domain.MovimentacaoEstoque;
import br.com.mmmodas.domain.Variacao;
import br.com.mmmodas.domain.enums.TipoMovimentacao;
import br.com.mmmodas.exception.EstoqueInsuficienteException;
import br.com.mmmodas.exception.RecursoNaoEncontradoException;
import br.com.mmmodas.exception.RegraDeNegocioException;
import br.com.mmmodas.repository.EstoqueRepository;
import br.com.mmmodas.repository.MovimentacaoEstoqueRepository;
import br.com.mmmodas.repository.VariacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Toda alteracao de saldo passa por aqui, e toda alteracao deixa rastro em
 * movimentacoes_estoque.
 *
 * PROPAGACAO: os metodos usam o padrao REQUIRED, ou seja, participam da
 * transacao de quem chamou. Isso e essencial no checkout: o pedido, a baixa de
 * estoque e o lancamento de auditoria precisam ser UM unico commit atomico.
 * Se a baixa do terceiro item falhar, os dois primeiros voltam junto - nunca
 * fica pedido criado sem estoque baixado, nem estoque baixado sem pedido.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("null")
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final VariacaoRepository variacaoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;

    /**
     * Da baixa e registra a saida. Lanca excecao se nao houver saldo, o que
     * desfaz a transacao inteira do checkout.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void baixar(UUID variacaoId, int quantidade, UUID pedidoId) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade a baixar deve ser positiva.");
        }

        int linhas = estoqueRepository.baixar(variacaoId, quantidade);

        if (linhas == 0) {
            // Nao havia saldo. Lemos o disponivel real apenas para compor a
            // mensagem de erro - a decisao ja foi tomada pelo banco.
            int disponivel = estoqueRepository.findById(variacaoId)
                    .map(e -> e.disponivel())
                    .orElse(0);
            throw new EstoqueInsuficienteException(variacaoId, quantidade, disponivel);
        }

        Variacao variacao = variacaoRepository.getReferenceById(variacaoId);
        movimentacaoRepository.save(MovimentacaoEstoque.de(
                variacao, TipoMovimentacao.SAIDA, -quantidade, pedidoId, "Baixa por venda"));
    }

    /** Entrada de mercadoria (reposicao de fornecedor). */
    @Transactional
    public void adicionar(UUID variacaoId, int quantidade, String motivo) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade de entrada deve ser positiva.");
        }
        exigirVariacao(variacaoId);

        // Se a variacao existe mas nao tem linha de estoque, o UPDATE afeta zero
        // linhas. Sem esta checagem, registraríamos uma entrada no livro-razao
        // que nunca aconteceu - auditoria mentindo e pior que auditoria ausente.
        if (estoqueRepository.adicionar(variacaoId, quantidade) == 0) {
            throw new RegraDeNegocioException("ESTOQUE_AUSENTE",
                    "Nao existe registro de estoque para a variacao " + variacaoId
                            + ". Crie o estoque antes de lancar entrada.");
        }

        movimentacaoRepository.save(MovimentacaoEstoque.de(
                variacaoRepository.getReferenceById(variacaoId),
                TipoMovimentacao.ENTRADA, quantidade, null, motivo));
    }

    /** Ajuste de inventario: define o saldo absoluto e registra a diferenca. */
    @Transactional
    public void ajustar(UUID variacaoId, int novaQuantidade, String motivo) {
        if (novaQuantidade < 0) {
            throw new IllegalArgumentException("Estoque nao pode ser negativo.");
        }
        Estoque atual = estoqueRepository.findById(variacaoId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Estoque da variacao", variacaoId));

        int diferenca = novaQuantidade - atual.getQuantidade();
        if (diferenca == 0) {
            return;
        }

        estoqueRepository.definirQuantidade(variacaoId, novaQuantidade);
        movimentacaoRepository.save(MovimentacaoEstoque.de(
                atual.getVariacao(), TipoMovimentacao.AJUSTE, diferenca, null,
                motivo == null ? "Ajuste de inventario" : motivo));
    }

    /**
     * Estorna o estoque de um pedido cancelado. Idempotente: se ja existe uma
     * movimentacao de ESTORNO para o pedido, nao estorna de novo.
     */
    @Transactional
    public void estornarPedido(UUID pedidoId, List<ItemEstorno> itens) {
        if (movimentacaoRepository.existsByPedidoIdAndTipo(pedidoId, TipoMovimentacao.ESTORNO)) {
            log.warn("Estorno do pedido {} ja havia sido feito; ignorando.", pedidoId);
            return;
        }
        for (ItemEstorno item : itens) {
            estoqueRepository.estornar(item.variacaoId(), item.quantidade());
            movimentacaoRepository.save(MovimentacaoEstoque.de(
                    variacaoRepository.getReferenceById(item.variacaoId()),
                    TipoMovimentacao.ESTORNO, item.quantidade(), pedidoId, "Estorno por cancelamento"));
        }
    }

    @Transactional(readOnly = true)
    public int disponivel(UUID variacaoId) {
        return estoqueRepository.findById(variacaoId).map(e -> e.disponivel()).orElse(0);
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoEstoque> historico(UUID variacaoId) {
        return movimentacaoRepository.findByVariacaoIdOrderByCriadoEmDesc(variacaoId);
    }

    private void exigirVariacao(UUID variacaoId) {
        if (!variacaoRepository.existsById(variacaoId)) {
            throw RecursoNaoEncontradoException.de("Variacao", variacaoId);
        }
    }

    /** Par (variacao, quantidade) para estorno. */
    public record ItemEstorno(UUID variacaoId, int quantidade) {
    }
}
