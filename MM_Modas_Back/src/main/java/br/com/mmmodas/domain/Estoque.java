package br.com.mmmodas.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Saldo de um SKU. Chave primaria compartilhada com variacoes: o proprio
 * variacao_id e a PK e a FK.
 *
 * O mapeamento usa coluna UUID atribuida + associacao somente-leitura, em vez
 * de @MapsId. @MapsId funciona, mas quando a associacao e carregada em cascata
 * ele sobrescreve o id de formas dificeis de depurar. Aqui o id e explicito.
 *
 * A baixa de estoque NAO passa por esta entidade: e feita por UPDATE nativo
 * condicional em EstoqueRepository.baixarEstoque, que e o que garante
 * atomicidade contra duas compras simultaneas do ultimo item.
 */
@Entity
@Table(name = "estoque")
@Getter
@Setter
@NoArgsConstructor
public class Estoque {

    @Id
    @Column(name = "variacao_id", nullable = false)
    private UUID variacaoId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variacao_id", insertable = false, updatable = false)
    private Variacao variacao;

    /** Quantidade fisica na prateleira. */
    @Column(name = "quantidade", nullable = false)
    private Integer quantidade = 0;

    /** Segurado por checkouts em andamento. */
    @Column(name = "reservado", nullable = false)
    private Integer reservado = 0;

    @Column(name = "estoque_minimo", nullable = false)
    private Integer estoqueMinimo = 0;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm;

    /** O que o cliente pode comprar agora. */
    public int disponivel() {
        return quantidade - reservado;
    }

    public boolean temSaldoPara(int qtd) {
        return disponivel() >= qtd;
    }

    public boolean abaixoDoMinimo() {
        return quantidade <= estoqueMinimo;
    }
}
