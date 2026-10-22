package br.com.mmmodas.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * SKU: a unidade que o cliente efetivamente compra ("Camiseta Oversized, M, Preta").
 * Preco e estoque vivem aqui, nao no produto.
 */
@Entity
@Table(name = "variacoes")
@Getter
@Setter
@NoArgsConstructor
public class Variacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(name = "sku", nullable = false, unique = true)
    private String sku;

    @Column(name = "tamanho", length = 20)
    private String tamanho;

    @Column(name = "cor", length = 40)
    private String cor;

    /** Nulo = herda o preco do produto (promocional, senao base). */
    @Column(name = "preco", precision = 12, scale = 2)
    private BigDecimal preco;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm;

    /**
     * Preco efetivo da variacao: proprio > promocional do produto > preco base.
     * Espelha exatamente a expressao COALESCE da view vw_variacoes_disponiveis.
     * Se mudar aqui, mude la - os dois precisam concordar.
     */
    public BigDecimal precoEfetivo() {
        if (preco != null) {
            return preco;
        }
        Produto p = this.produto;
        if (p.getPrecoPromocional() != null) {
            return p.getPrecoPromocional();
        }
        return p.getPrecoBase();
    }

    public String descricaoCompleta() {
        StringBuilder sb = new StringBuilder(produto.getNome());
        if (tamanho != null) {
            sb.append(" / ").append(tamanho);
        }
        if (cor != null) {
            sb.append(" / ").append(cor);
        }
        return sb.toString();
    }
}
