package br.com.mmmodas.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "categorias")
@Getter
@Setter
@NoArgsConstructor
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    /** Categoria pai, para hierarquia (Feminino > Vestidos). Nulo = raiz. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Categoria parent;

    @Column(name = "ordem", nullable = false)
    private Integer ordem = 0;

    @Column(name = "ativa", nullable = false)
    private boolean ativa = true;
}
