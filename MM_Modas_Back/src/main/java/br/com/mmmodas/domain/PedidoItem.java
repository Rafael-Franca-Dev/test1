package br.com.mmmodas.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Item vendido. Guarda copia de nome/sku/tamanho/cor/preco: se o produto mudar
 * de preco ou sair do catalogo amanha, a nota do pedido continua correta.
 *
 * total_item e coluna gerada pelo banco (quantidade * preco_unitario), portanto
 * insertable=false / updatable=false. O Java so le.
 */
@Entity
@Table(name = "pedido_itens")
@Getter
@Setter
@NoArgsConstructor
public class PedidoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variacao_id", nullable = false)
    private Variacao variacao;

    @Column(name = "produto_nome", nullable = false)
    private String produtoNome;

    @Column(name = "sku", nullable = false)
    private String sku;

    @Column(name = "tamanho", length = 20)
    private String tamanho;

    @Column(name = "cor", length = 40)
    private String cor;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "preco_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precoUnitario;

    /** Coluna GENERATED ALWAYS AS ... STORED no PostgreSQL. Nunca escrita pelo Java. */
    @Column(name = "total_item", insertable = false, updatable = false, precision = 12, scale = 2)
    private BigDecimal totalItem;

    /** Preenche o snapshot a partir da variacao no momento da venda. */
    public static PedidoItem de(Variacao v, int quantidade, BigDecimal precoUnitario) {
        PedidoItem item = new PedidoItem();
        item.setVariacao(v);
        item.setProdutoNome(v.getProduto().getNome());
        item.setSku(v.getSku());
        item.setTamanho(v.getTamanho());
        item.setCor(v.getCor());
        item.setQuantidade(quantidade);
        item.setPrecoUnitario(precoUnitario);
        return item;
    }
}
