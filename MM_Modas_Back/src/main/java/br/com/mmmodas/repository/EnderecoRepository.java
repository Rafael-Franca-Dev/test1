package br.com.mmmodas.repository;

import br.com.mmmodas.domain.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, UUID> {

    List<Endereco> findByClienteIdOrderByPrincipalDescCriadoEmDesc(UUID clienteId);

    /** Busca por id E dono: impede um cliente ler o endereco de outro. */
    Optional<Endereco> findByIdAndClienteId(UUID id, UUID clienteId);

    /**
     * Remove a marca de principal antes de eleger outro. Obrigatorio: existe o
     * indice unico parcial uq_enderecos_principal_por_cliente, que recusaria
     * dois principais para o mesmo cliente.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Endereco e set e.principal = false where e.cliente.id = :clienteId and e.principal = true")
    int limparPrincipal(@Param("clienteId") UUID clienteId);

    long countByClienteId(UUID clienteId);
}
