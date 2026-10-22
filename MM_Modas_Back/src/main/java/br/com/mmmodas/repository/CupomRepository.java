package br.com.mmmodas.repository;

import br.com.mmmodas.domain.Cupom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CupomRepository extends JpaRepository<Cupom, UUID> {

    /** Codigo gravado em maiusculas, mas aceitamos o que o cliente digitar. */
    Optional<Cupom> findByCodigoIgnoreCase(String codigo);

    /**
     * Consome uma unidade do cupom de forma atomica. O "AND (usos_maximos IS NULL
     * OR usos_realizados < usos_maximos)" impede que dois pedidos simultaneos
     * usem a ultima unidade de um cupom limitado.
     */
    @Modifying(flushAutomatically = true)
    @Query(value = """
            UPDATE cupons
               SET usos_realizados = usos_realizados + 1
             WHERE id = :cupomId
               AND ativo = true
               AND (usos_maximos IS NULL OR usos_realizados < usos_maximos)
            """, nativeQuery = true)
    int consumir(@Param("cupomId") UUID cupomId);

    @Modifying(flushAutomatically = true)
    @Query(value = "UPDATE cupons SET usos_realizados = GREATEST(usos_realizados - 1, 0) WHERE id = :cupomId", nativeQuery = true)
    int devolver(@Param("cupomId") UUID cupomId);
}
