package br.com.mmmodas.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "produtos")
@Getter
@Setter
@NoArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "descricao", columnDefinition = "text")
    private String descricao;

    @Column(name = "descricao_curta")
    private String descricaoCurta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @Column(name = "marca")
    private String marca;

    @Column(name = "preco_base", nullable = false, precision = 12, scale = 2)
    private BigDecimal precoBase;

    /** Preco promocional. Nunca maior que precoBase (o banco tambem garante). */
    @Column(name = "preco_promocional", precision = 12, scale = 2)
    private BigDecimal precoPromocional;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @Column(name = "destaque", nullable = false)
    private boolean destaque = false;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProdutoImagem> imagens = new ArrayList<>();

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Variacao> variacoes = new ArrayList<>();

    public void addImagem(ProdutoImagem imagem) {
        imagem.setProduto(this);
        this.imagens.add(imagem);
    }

    public void addVariacao(Variacao variacao) {
        variacao.setProduto(this);
        this.variacoes.add(variacao);
    }
}
