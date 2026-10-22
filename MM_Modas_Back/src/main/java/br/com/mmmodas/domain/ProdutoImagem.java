package br.com.mmmodas.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "produto_imagens")
@Getter
@Setter
@NoArgsConstructor
public class ProdutoImagem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(name = "url", nullable = false, columnDefinition = "text")
    private String url;

    /** Texto alternativo, acessibilidade e SEO. */
    @Column(name = "alt")
    private String alt;

    @Column(name = "ordem", nullable = false)
    private Integer ordem = 0;

    @Column(name = "principal", nullable = false)
    private boolean principal = false;
}
