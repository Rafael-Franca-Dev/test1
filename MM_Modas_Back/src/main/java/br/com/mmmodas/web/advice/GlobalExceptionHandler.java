package br.com.mmmodas.web.advice;

import br.com.mmmodas.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Traduz excecoes de dominio e de validacao em respostas JSON padronizadas.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> tratarNaoEncontrado(RecursoNaoEncontradoException ex) {
        return resposta(HttpStatus.NOT_FOUND, "NAO_ENCONTRADO", ex.getMessage(), null);
    }

    @ExceptionHandler(NaoAutorizadoException.class)
    public ResponseEntity<Map<String, Object>> tratarNaoAutorizado(NaoAutorizadoException ex) {
        return resposta(HttpStatus.UNAUTHORIZED, "NAO_AUTORIZADO", ex.getMessage(), null);
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<Map<String, Object>> tratarConflito(ConflitoException ex) {
        return resposta(HttpStatus.CONFLICT, "CONFLITO", ex.getMessage(), null);
    }

    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> tratarEstoqueInsuficiente(EstoqueInsuficienteException ex) {
        Map<String, Object> detalhes = Map.of(
                "variacaoId", ex.getVariacaoId(),
                "solicitado", ex.getSolicitado(),
                "disponivel", ex.getDisponivel()
        );
        return resposta(HttpStatus.UNPROCESSABLE_ENTITY, ex.getCodigo(), ex.getMessage(), detalhes);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<Map<String, Object>> tratarRegraDeNegocio(RegraDeNegocioException ex) {
        return resposta(HttpStatus.UNPROCESSABLE_ENTITY, ex.getCodigo(), ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> tratarValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            campos.putIfAbsent(erro.getField(), erro.getDefaultMessage());
        }
        return resposta(HttpStatus.BAD_REQUEST, "DADOS_INVALIDOS", "Verifique os campos informados.", campos);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> tratarArgumentoInvalido(IllegalArgumentException ex) {
        return resposta(HttpStatus.BAD_REQUEST, "ARGUMENTO_INVALIDO", ex.getMessage(), null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> tratarViolacaoIntegridade(DataIntegrityViolationException ex) {
        log.warn("Violacao de integridade no banco: {}", ex.getMostSpecificCause().getMessage());
        return resposta(HttpStatus.CONFLICT, "CONFLITO_INTEGRIDADE",
                "Operacao em conflito com um registro ja existente.", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> tratarErroInesperado(Exception ex) {
        log.error("Erro inesperado na requisicao", ex);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "ERRO_INTERNO",
                "Ocorreu um erro interno no servidor.", null);
    }

    private ResponseEntity<Map<String, Object>> resposta(HttpStatus status, String codigo,
                                                         String mensagem, Object detalhes) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("codigo", codigo);
        body.put("mensagem", mensagem);
        if (detalhes != null) {
            body.put("detalhes", detalhes);
        }
        body.put("timestamp", OffsetDateTime.now());
        return ResponseEntity.status(status).body(body);
    }
}

