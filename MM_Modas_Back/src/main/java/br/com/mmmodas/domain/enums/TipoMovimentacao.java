package br.com.mmmodas.domain.enums;

/**
 * Natureza de cada lancamento de estoque.
 * quantidade positiva = entra, negativa = sai.
 */
public enum TipoMovimentacao {
    ENTRADA,
    SAIDA,
    AJUSTE,
    RESERVA,
    LIBERACAO_RESERVA,
    ESTORNO
}
