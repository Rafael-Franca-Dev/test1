package br.com.mmmodas.repository;

import br.com.mmmodas.domain.Estoque;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, UUID> {

    /**
     * A OPERACAO MAIS IMPORTANTE DO SISTEMA.
     *
     * Da baixa no estoque de forma atomica. O "AND (quantidade - reservado) >=
     * :quantidade" e o que impede vender o que nao existe: o proprio banco decide
     * se ha saldo, dentro de uma unica instrucao.
     *
     * Por que nao ler, comparar em Java e depois gravar: entre a leitura e a
     * gravacao, outra requisicao pode ter vendido a mesma peca. Esse padrao
     * (check-then-act) e exatamente como se vende 1 item 3 vezes.
     *
     * A regra de LINHAS AFETADAS decide o resultado:
     *   retorno >= 1 -> baixou, pode seguir
     *   retorno == 0 -> nao havia saldo suficiente, abortar a transacao
     *
     * O CHECK ck_estoque_quantidade (quantidade >= 0) e a ultima barreira: mesmo
     * se esta consulta tivesse um bug, o banco recusaria saldo negativo.
     */
    @Modifying(flushAutomatically = true)
    @Query(value = """
            UPDATE estoque
               SET quantidade    = quantidade - :quantidade,
                   atualizado_em = now()
             WHERE variacao_id   = :variacaoId
               AND (quantidade - reservado) >= :quantidade
            """, nativeQuery = true)
    int baixar(@Param("variacaoId") UUID variacaoId, @Param("quantidade") int quantidade);

    /** Devolve ao estoque no cancelamento ou estorno. Soma e sempre seguro. */
    @Modifying(flushAutomatically = true)
    @Query(value = """
            UPDATE estoque
               SET quantidade    = quantidade + :quantidade,
                   atualizado_em = now()
             WHERE variacao_id   = :variacaoId
            """, nativeQuery = true)
    int estornar(@Param("variacaoId") UUID variacaoId, @Param("quantidade") int quantidade);

    /** Ajuste manual do administrador: define o saldo absoluto. */
    @Modifying(flushAutomatically = true)
    @Query(value = """
            UPDATE estoque
               SET quantidade    = :quantidade,
                   atualizado_em = now()
             WHERE variacao_id   = :variacaoId
            """, nativeQuery = true)
    int definirQuantidade(@Param("variacaoId") UUID variacaoId, @Param("quantidade") int quantidade);

    /** Entrada de mercadoria. */
    @Modifying(flushAutomatically = true)
    @Query(value = """
            UPDATE estoque
               SET quantidade    = quantidade + :quantidade,
                   atualizado_em = now()
             WHERE variacao_id   = :variacaoId
            """, nativeQuery = true)
    int adicionar(@Param("variacaoId") UUID variacaoId, @Param("quantidade") int quantidade);

    /**
     * Leitura com trava de linha. Usada so onde a decisao depende de ler o saldo
     * antes de agir (ex.: montar a resposta de disponibilidade no checkout).
     * Para dar baixa, use baixar() - nao precisa de SELECT FOR UPDATE.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Estoque e where e.variacaoId = :variacaoId")
    Optional<Estoque> buscarComTrava(@Param("variacaoId") UUID variacaoId);

    @Query("select e.variacaoId from Estoque e where e.quantidade <= e.estoqueMinimo")
    List<UUID> idsAbaixoDoMinimo();
}
