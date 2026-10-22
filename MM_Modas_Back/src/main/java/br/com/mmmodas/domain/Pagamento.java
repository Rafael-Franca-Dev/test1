package br.com.mmmodas.domain;

import br.com.mmmodas.domain.enums.MetodoPagamento;
import br.com.mmmodas.domain.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Tentativa de pagamento. Um pedido pode ter varias (Pix gerado, expirado,
 * cartao recusado, nova tentativa aprovada) - por isso e tabela, nao coluna.
 */
@Entity
@Table(name = "pagamentos")
@Getter
@Setter
@NoArgsConstructor
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo", nullable = false, length = 30)
    private MetodoPagamento metodo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusPagamento status = StatusPagamento.PENDENTE;

    @Column(name = "valor", nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(name = "parcelas", nullable = false)
    private Integer parcelas = 1;

    /** 'MERCADO_PAGO'. Coluna existe para permitir trocar de gateway sem migrar. */
    @Column(name = "provider", length = 30)
    private String provider;

    /** id/transaction_amount ref retornado pelo gateway. */
    @Column(name = "provider_txid")
    private String providerTxid;

    /** Resposta crua do gateway, para auditoria e depuracao de divergencia. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb")
    private String payload;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm;

    public boolean isAprovado() {
        return status == StatusPagamento.APROVADO;
    }
}
