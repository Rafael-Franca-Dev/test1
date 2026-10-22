package br.com.mmmodas.exception;

import java.util.UUID;

/**
 * Saldo insuficiente no momento da baixa.
 *
 * Distinta de RegraDeNegocioException de proposito: o front precisa reagir de
 * forma especifica (destacar QUAL item acabou, sugerir quantidade menor),
 * nao apenas mostrar um erro generico.
 */
public class EstoqueInsuficienteException extends RegraDeNegocioException {

    private final UUID variacaoId;
    private final int solicitado;
    private final int disponivel;

    public EstoqueInsuficienteException(UUID variacaoId, int solicitado, int disponivel) {
        super("ESTOQUE_INSUFICIENTE", String.format(
                "Estoque insuficiente. Solicitado: %d, disponivel: %d.", solicitado, disponivel));
        this.variacaoId = variacaoId;
        this.solicitado = solicitado;
        this.disponivel = disponivel;
    }

    public UUID getVariacaoId() {
        return variacaoId;
    }

    public int getSolicitado() {
        return solicitado;
    }

    public int getDisponivel() {
        return disponivel;
    }
}
