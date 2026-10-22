package br.com.mmmodas.domain;

import br.com.mmmodas.domain.enums.TipoMovimentacao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Livro-razao do estoque. Toda alteracao de saldo gera uma linha aqui.
 *
 * Sem esta tabela, quando o estoque do sistema divergir da prateleira nao ha
 * como responder "quem mexeu, quando e por que". E a unica forma de auditar
 * uma venda, um ajuste manual ou um cancelamento.
 */
@Entity
@Table(name = "movimentacoes_estoque")
@Getter
@Setter
@NoArgsConstructor
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variacao_id", nullable = false)
    private Variacao variacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoMovimentacao tipo;

    /** Negativo = saida, positivo = entrada. Nunca zero. */
    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    /** Referencia solta: a movimentacao nao precisa carregar o pedido inteiro. */
    @Column(name = "pedido_id")
    private UUID pedidoId;

    @Column(name = "motivo", columnDefinition = "text")
    private String motivo;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    public static MovimentacaoEstoque de(Variacao variacao, TipoMovimentacao tipo,
                                         int quantidade, UUID pedidoId, String motivo) {
        MovimentacaoEstoque m = new MovimentacaoEstoque();
        m.setVariacao(variacao);
        m.setTipo(tipo);
        m.setQuantidade(quantidade);
        m.setPedidoId(pedidoId);
        m.setMotivo(motivo);
        return m;
    }
}
