package br.com.mmmodas.repository;

import br.com.mmmodas.domain.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

    Optional<Categoria> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Categoria> findByAtivaTrueOrderByOrdemAscNomeAsc();
}
