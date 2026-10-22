package br.com.mmmodas.exception;

/** Credenciais invalidas ou token expirado. Vira HTTP 401. */
public class NaoAutorizadoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NaoAutorizadoException(String mensagem) {
        super(mensagem);
    }
}
