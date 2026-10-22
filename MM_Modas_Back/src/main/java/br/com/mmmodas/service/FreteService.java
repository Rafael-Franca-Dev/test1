package br.com.mmmodas.service;

import br.com.mmmodas.exception.RegraDeNegocioException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;

/**
 * Calculo de frete.
 *
 * POR QUE O FRETE E CALCULADO NO SERVIDOR: o valor nao pode vir pronto do front.
 * Se aceitassemos "valorFrete" do navegador, qualquer pessoa mudaria o frete para
 * R$ 0,00 no DevTools antes de finalizar. Preco definido pelo cliente e a falha
 * mais comum e mais cara nesse tipo de sistema.
 *
 * O front deve exibir o valor obtido em POST /api/frete/simular, para que o que o
 * cliente ve na tela seja exatamente o que o servidor vai cobrar.
 *
 * Regra provisoria: gratis acima de R$ 299, senao R$ 24,90 + adicional por regiao.
 * Trocar por uma integracao real (Correios, Melhor Envio) exige mexer so aqui.
 */
@Service
public class FreteService {

    private static final BigDecimal LIMITE_FRETE_GRATIS = new BigDecimal("299.00");
    private static final BigDecimal FRETE_BASE = new BigDecimal("24.90");

    /** Adicional por regiao, somado ao frete base. */
    private static final Map<String, BigDecimal> ADICIONAL_POR_UF = Map.of(
            "SP", new BigDecimal("0.00"),
            "RJ", new BigDecimal("5.00"),
            "MG", new BigDecimal("5.00"),
            "ES", new BigDecimal("7.00"),
            "PR", new BigDecimal("7.00"),
            "SC", new BigDecimal("9.00"),
            "RS", new BigDecimal("9.00")
    );
    private static final BigDecimal ADICIONAL_PADRAO = new BigDecimal("15.00");
    private static final Set<String> UFS_VALIDAS = Set.of(
            "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS",
            "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC",
            "SP", "SE", "TO");

    public BigDecimal calcular(BigDecimal subtotal, String uf) {
        if (subtotal == null) {
            throw new RegraDeNegocioException("Subtotal invalido para calculo de frete.");
        }
        if (subtotal.compareTo(LIMITE_FRETE_GRATIS) >= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        String regiao = normalizarUf(uf);
        BigDecimal adicional = ADICIONAL_POR_UF.getOrDefault(regiao, ADICIONAL_PADRAO);
        return FRETE_BASE.add(adicional).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal limiteFreteGratis() {
        return LIMITE_FRETE_GRATIS;
    }

    private String normalizarUf(String uf) {
        if (uf == null || uf.isBlank()) {
            throw new RegraDeNegocioException("UF de entrega e obrigatoria.");
        }
        String normalizada = uf.trim().toUpperCase();
        if (!UFS_VALIDAS.contains(normalizada)) {
            throw new RegraDeNegocioException("UF de entrega invalida: " + uf);
        }
        return normalizada;
    }
}
