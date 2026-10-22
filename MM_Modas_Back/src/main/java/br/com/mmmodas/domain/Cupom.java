package br.com.mmmodas.domain;

import br.com.mmmodas.domain.enums.TipoCupom;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "cupons")
@Getter
@Setter
@NoArgsConstructor
public class Cupom {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Gravado sempre em MAIUSCULAS pelo servico. */
    @Column(name = "codigo", nullable = false, unique = true, length = 40)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoCupom tipo;

    /** PERCENTUAL: 0-100. VALOR_FIXO: reais. FRETE_GRATIS: ignorado. */
    @Column(name = "valor", nullable = false, precision = 12, scale = 2)
    private BigDecimal valor = BigDecimal.ZERO;

    @Column(name = "valor_minimo", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorMinimo = BigDecimal.ZERO;

    @Column(name = "usos_maximos")
    private Integer usosMaximos;

    @Column(name = "usos_realizados", nullable = false)
    private Integer usosRealizados = 0;

    @Column(name = "valido_de", nullable = false)
    private OffsetDateTime validoDe = OffsetDateTime.now();

    @Column(name = "valido_ate")
    private OffsetDateTime validoAte;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    /**
     * Todas as regras de uso em um lugar so, avaliadas com um instante explicito
     * para o calculo ser testavel sem depender do relogio da maquina.
     */
    public boolean podeSerUsado(BigDecimal subtotal, OffsetDateTime agora) {
        if (!ativo) {
            return false;
        }
        if (validoDe != null && agora.isBefore(validoDe)) {
            return false;
        }
        if (validoAte != null && agora.isAfter(validoAte)) {
            return false;
        }
        if (usosMaximos != null && usosRealizados >= usosMaximos) {
            return false;
        }
        if (tipo == TipoCupom.PERCENTUAL && valor.compareTo(BigDecimal.valueOf(100)) > 0) {
            return false;
        }
        return subtotal.compareTo(valorMinimo) >= 0;
    }

    /**
     * Desconto a aplicar sobre o subtotal. Nunca devolve mais que o subtotal:
     * cupom de R$ 100 num carrinho de R$ 30 nao pode virar credito de R$ 70.
     */
    public BigDecimal calcularDesconto(BigDecimal subtotal) {
        BigDecimal percentualSeguro = valor.min(BigDecimal.valueOf(100)).max(BigDecimal.ZERO);
        BigDecimal desconto = switch (tipo) {
            case PERCENTUAL -> subtotal.multiply(percentualSeguro).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            case VALOR_FIXO -> valor;
            case FRETE_GRATIS -> BigDecimal.ZERO;
        };
        return desconto.min(subtotal).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isFreteGratis() {
        return tipo == TipoCupom.FRETE_GRATIS;
    }
}
