package br.com.mmmodas.exception;

/** Duplicidade ou estado conflitante (e-mail ja cadastrado, pedido ja pago). Vira HTTP 409. */
public class ConflitoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
