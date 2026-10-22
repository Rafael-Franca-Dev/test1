package br.com.mmmodas.exception;

/** Recurso inexistente ou que nao pertence ao usuario autenticado. Vira HTTP 404. */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public static RecursoNaoEncontradoException de(String recurso, Object id) {
        return new RecursoNaoEncontradoException(String.format("%s nao encontrado: %s", recurso, id));
    }
}
