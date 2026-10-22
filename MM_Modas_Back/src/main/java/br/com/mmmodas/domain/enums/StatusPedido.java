package br.com.mmmodas.domain.enums;

/**
 * Ciclo de vida do pedido.
 *
 * Transicoes validas:
 *   PENDENTE   -> PAGO, CANCELADO
 *   PAGO       -> EM_SEPARACAO, CANCELADO, REEMBOLSADO
 *   EM_SEPARACAO -> ENVIADO, CANCELADO
 *   ENVIADO    -> ENTREGUE
 *   ENTREGUE   -> REEMBOLSADO
 *
 * Os nomes batem exatamente com o CHECK ck_pedidos_status da migracao V1.
 * Renomear aqui sem migracao quebra a aplicacao na subida (ddl-auto=validate).
 */
public enum StatusPedido {
    PENDENTE,
    PAGO,
    EM_SEPARACAO,
    ENVIADO,
    ENTREGUE,
    CANCELADO,
    REEMBOLSADO;

    /** Estados em que o estoque ja foi baixado e nao pode ser baixado de novo. */
    public boolean estoqueJaBaixado() {
        return this != PENDENTE && this != CANCELADO;
    }

    public boolean aceitaCancelamento() {
        return this == PENDENTE || this == PAGO || this == EM_SEPARACAO;
    }
}
