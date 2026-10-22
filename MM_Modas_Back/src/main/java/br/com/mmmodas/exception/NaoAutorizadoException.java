package br.com.mmmodas.exception;

/** Credenciais invalidas ou token expirado. Vira HTTP 401. */
public class NaoAutorizadoException extends RuntimeException {

    public NaoAutorizadoException(String mensagem) {
        super(mensagem);
    }
}
