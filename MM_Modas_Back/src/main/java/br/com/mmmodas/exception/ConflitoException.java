package br.com.mmmodas.exception;

/** Duplicidade ou estado conflitante (e-mail ja cadastrado, pedido ja pago). Vira HTTP 409. */
public class ConflitoException extends RuntimeException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
