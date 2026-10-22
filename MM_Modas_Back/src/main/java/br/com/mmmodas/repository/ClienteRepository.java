package br.com.mmmodas.repository;

import br.com.mmmodas.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    /** IgnoreCase casa com o indice funcional uq_clientes_email_lower. */
    Optional<Cliente> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByCpf(String cpf);
}
