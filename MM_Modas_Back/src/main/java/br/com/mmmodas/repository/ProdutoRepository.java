package br.com.mmmodas.repository;

import br.com.mmmodas.domain.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProdutoRepository extends JpaRepository<Produto, UUID> {

    Optional<Produto> findBySlug(String slug);

    @Query("""
            select distinct p from Produto p
              left join fetch p.categoria c
              left join fetch p.imagens img
             where p.ativo = true
             order by p.destaque desc, p.criadoEm desc
            """)
    List<Produto> listarAtivosComCategoriaEImagens();

    @Query("""
            select distinct p from Produto p
              left join fetch p.categoria c
              left join fetch p.imagens img
             where p.slug = :slug and p.ativo = true
            """)
    Optional<Produto> buscarAtivoPorSlugComDetalhes(@Param("slug") String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, UUID id);
}
