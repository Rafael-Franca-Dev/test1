package br.com.mmmodas.exception;

/** Erro de regra de negocio: entrada valida, operacao recusada. Vira HTTP 422. */
public class RegraDeNegocioException extends RuntimeException {

    private final String codigo;

    public RegraDeNegocioException(String mensagem) {
        this("REGRA_NEGOCIO", mensagem);
    }

    public RegraDeNegocioException(String codigo, String mensagem) {
        super(mensagem);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
