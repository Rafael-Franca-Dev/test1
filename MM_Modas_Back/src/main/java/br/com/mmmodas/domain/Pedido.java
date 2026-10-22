package br.com.mmmodas.domain;

import br.com.mmmodas.domain.enums.StatusPedido;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.generator.EventType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Pedido confirmado. O endereco de entrega e copiado (snapshot) em vez de
 * referenciado: o cliente pode editar ou apagar o endereco depois, e o pedido
 * precisa continuar mostrando para onde foi enviado.
 */
@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Gerado pelo banco via sequence ('MM-001000'). @Generated(INSERT) faz o
     * Hibernate omitir a coluna no INSERT e reler o valor depois - sem isso ele
     * mandaria NULL e violaria o NOT NULL.
     */
    @Generated(event = EventType.INSERT)
    @Column(name = "numero", insertable = false, updatable = false)
    private String numero;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusPedido status = StatusPedido.PENDENTE;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "desconto", nullable = false, precision = 12, scale = 2)
    private BigDecimal desconto = BigDecimal.ZERO;

    @Column(name = "valor_frete", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorFrete = BigDecimal.ZERO;

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cupom_id")
    private Cupom cupom;

    // --- Snapshot do endereco de entrega ---
    @Column(name = "entrega_destinatario", nullable = false)
    private String entregaDestinatario;

    @Column(name = "entrega_cep", nullable = false, length = 8)
    private String entregaCep;

    @Column(name = "entrega_logradouro", nullable = false)
    private String entregaLogradouro;

    @Column(name = "entrega_numero", nullable = false, length = 20)
    private String entregaNumero;

    @Column(name = "entrega_complemento")
    private String entregaComplemento;

    @Column(name = "entrega_bairro", nullable = false)
    private String entregaBairro;

    @Column(name = "entrega_cidade", nullable = false)
    private String entregaCidade;

    @Column(name = "entrega_uf", nullable = false, length = 2)
    private String entregaUf;

    /**
     * Chave enviada pelo front. O UNIQUE no banco e a barreira final contra
     * pedido duplicado quando o usuario clica duas vezes ou a rede reenvia.
     */
    @Column(name = "idempotency_key", length = 100)
    private String idempotencyKey;

    @Column(name = "observacoes", columnDefinition = "text")
    private String observacoes;

    @Column(name = "pago_em")
    private OffsetDateTime pagoEm;

    @Column(name = "enviado_em")
    private OffsetDateTime enviadoEm;

    @Column(name = "entregue_em")
    private OffsetDateTime entregueEm;

    @Column(name = "cancelado_em")
    private OffsetDateTime canceladoEm;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PedidoItem> itens = new ArrayList<>();

    public void addItem(PedidoItem item) {
        item.setPedido(this);
        this.itens.add(item);
    }

    public void copiarEnderecoDe(Endereco e) {
        this.entregaDestinatario = e.getDestinatario();
        this.entregaCep = e.getCep();
        this.entregaLogradouro = e.getLogradouro();
        this.entregaNumero = e.getNumero();
        this.entregaComplemento = e.getComplemento();
        this.entregaBairro = e.getBairro();
        this.entregaCidade = e.getCidade();
        this.entregaUf = e.getUf();
    }

    /** subtotal - desconto + frete, arredondado para centavos. */
    public void recalcularTotal() {
        this.total = this.subtotal
                .subtract(this.desconto)
                .add(this.valorFrete)
                .setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
