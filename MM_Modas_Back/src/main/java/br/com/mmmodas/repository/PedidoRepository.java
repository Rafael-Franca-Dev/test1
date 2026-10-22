package br.com.mmmodas.repository;

import br.com.mmmodas.domain.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    Optional<Pedido> findByNumero(String numero);

    Optional<Pedido> findByIdempotencyKey(String idempotencyKey);

    Page<Pedido> findByClienteIdOrderByCriadoEmDesc(UUID clienteId, Pageable pageable);

    @Query("""
            select distinct p from Pedido p
              left join fetch p.itens i
             where p.id = :id
            """)
    Optional<Pedido> buscarComItens(@Param("id") UUID id);

    @Query("""
            select distinct p from Pedido p
              left join fetch p.itens i
             where p.numero = :numero
            """)
    Optional<Pedido> buscarComItensPorNumero(@Param("numero") String numero);

    /**
     * Transicao PENDENTE -> PAGO condicional, atomica.
     *
     * Esta e a trava que torna o webhook do Mercado Pago idempotente. O gateway
     * REENVIA a mesma notificacao varias vezes; sem esta condicao, cada reenvio
     * baixaria o estoque de novo e o pedido viraria uma venda fantasma.
     *
     * retorno == 0 -> ja estava pago (ou cancelado): e duplicata, ignore
     * retorno == 1 -> e a primeira vez: pode baixar estoque
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
            UPDATE pedidos
               SET status = 'PAGO', pago_em = :agora, atualizado_em = :agora
             WHERE id = :pedidoId AND status = 'PENDENTE'
            """, nativeQuery = true)
    int marcarComoPago(@Param("pedidoId") UUID pedidoId, @Param("agora") OffsetDateTime agora);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
            UPDATE pedidos
               SET status = 'CANCELADO', cancelado_em = :agora, atualizado_em = :agora
             WHERE id = :pedidoId AND status IN ('PENDENTE', 'PAGO', 'EM_SEPARACAO')
            """, nativeQuery = true)
    int marcarComoCancelado(@Param("pedidoId") UUID pedidoId, @Param("agora") OffsetDateTime agora);

    /** Verdadeiro se o estoque deste pedido ja foi baixado alguma vez. */
    @Query(value = """
            SELECT EXISTS (
                SELECT 1 FROM movimentacoes_estoque
                 WHERE pedido_id = :pedidoId AND tipo = 'SAIDA'
            )
            """, nativeQuery = true)
    boolean estoqueJaFoiBaixado(@Param("pedidoId") UUID pedidoId);
}
