package br.com.mmmodas.repository;

import br.com.mmmodas.domain.MovimentacaoEstoque;
import br.com.mmmodas.domain.enums.TipoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    List<MovimentacaoEstoque> findByVariacaoIdOrderByCriadoEmDesc(UUID variacaoId);

    List<MovimentacaoEstoque> findByPedidoId(UUID pedidoId);

    /** Usado para saber se o estoque daquele pedido ja foi baixado antes de estornar. */
    boolean existsByPedidoIdAndTipo(UUID pedidoId, TipoMovimentacao tipo);
}
