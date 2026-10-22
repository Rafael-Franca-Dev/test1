package br.com.mmmodas.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "carrinho_itens")
@Getter
@Setter
@NoArgsConstructor
public class CarrinhoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrinho_id", nullable = false)
    private Carrinho carrinho;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variacao_id", nullable = false)
    private Variacao variacao;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    /** Preco no momento de adicionar. O preco que vale e o reconferido no checkout. */
    @Column(name = "preco_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precoUnitario;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    public BigDecimal total() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
}
