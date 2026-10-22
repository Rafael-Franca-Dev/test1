package br.com.mmmodas.repository;

import br.com.mmmodas.domain.Pagamento;
import br.com.mmmodas.domain.enums.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PagamentoRepository extends JpaRepository<Pagamento, UUID> {

    List<Pagamento> findByPedidoIdOrderByCriadoEmDesc(UUID pedidoId);

    /** Localiza a tentativa a partir da notificacao do gateway. */
    Optional<Pagamento> findByProviderAndProviderTxid(String provider, String providerTxid);

    Optional<Pagamento> findFirstByPedidoIdAndStatusOrderByCriadoEmDesc(UUID pedidoId, StatusPagamento status);
}
