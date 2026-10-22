package br.com.mmmodas.repository;

import br.com.mmmodas.domain.Carrinho;
import br.com.mmmodas.domain.enums.StatusCarrinho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CarrinhoRepository extends JpaRepository<Carrinho, UUID> {

    Optional<Carrinho> findFirstByClienteIdAndStatusOrderByCriadoEmDesc(UUID clienteId, StatusCarrinho status);

    Optional<Carrinho> findFirstBySessionTokenAndStatusOrderByCriadoEmDesc(String sessionToken, StatusCarrinho status);

    /**
     * Carrega carrinho + itens + variacao + produto numa consulta. Sem isso, montar
     * o carrinho com 10 itens dispara mais de 20 SELECTs (problema N+1).
     * Seguro porque devolve um unico resultado - nao ha paginacao envolvida.
     */
    @Query("""
            select distinct c from Carrinho c
              left join fetch c.itens i
              left join fetch i.variacao v
              left join fetch v.produto p
             where c.id = :id
            """)
    Optional<Carrinho> buscarComItens(@Param("id") UUID id);
}
