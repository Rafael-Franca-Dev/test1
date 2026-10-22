package br.com.mmmodas.domain;

import br.com.mmmodas.domain.enums.StatusCarrinho;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Carrinho ativo. Existe para cliente logado OU para visitante identificado por
 * session_token - o CHECK ck_carrinhos_dono garante que nunca ha carrinho sem dono.
 */
@Entity
@Table(name = "carrinhos")
@Getter
@Setter
@NoArgsConstructor
public class Carrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    /** Para carrinho de visitante, antes do login. */
    @Column(name = "session_token", length = 100)
    private String sessionToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusCarrinho status = StatusCarrinho.ATIVO;

    @Column(name = "expira_em")
    private OffsetDateTime expiraEm;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm;

    @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CarrinhoItem> itens = new ArrayList<>();

    public void addItem(CarrinhoItem item) {
        item.setCarrinho(this);
        this.itens.add(item);
    }

    public void removerItem(CarrinhoItem item) {
        this.itens.remove(item);
        item.setCarrinho(null);
    }
}
