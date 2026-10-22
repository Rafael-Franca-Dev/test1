package br.com.mmmodas.repository;

import br.com.mmmodas.domain.Variacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VariacaoRepository extends JpaRepository<Variacao, UUID> {

    Optional<Variacao> findBySku(String sku);

    boolean existsBySku(String sku);

    List<Variacao> findByProdutoIdOrderByTamanhoAscCorAsc(UUID produtoId);

    /**
     * Carrega a variacao com produto e imagens em uma consulta. Usado no checkout,
     * onde precisamos do nome do produto e do preco sem disparar N+1 por item.
     */
    @Query("""
            select v from Variacao v
              join fetch v.produto p
             where v.id in :ids
            """)
    List<Variacao> buscarComProduto(@Param("ids") List<UUID> ids);
}
