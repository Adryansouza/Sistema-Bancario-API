package com.adryan.projetobanco.exception;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiError> regraDeNegocio(IllegalArgumentException e) {
        return resposta(HttpStatus.BAD_REQUEST, "REGRA_DE_NEGOCIO", e.getMessage());
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    ResponseEntity<ApiError> conflito(SQLIntegrityConstraintViolationException e) {
        return resposta(HttpStatus.CONFLICT, "DADO_DUPLICADO", "O documento ou a chave PIX ja esta cadastrado.");
    }

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<ApiError> erroInterno(RuntimeException e) {
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "ERRO_INTERNO", e.getMessage());
    }

    private ResponseEntity<ApiError> resposta(HttpStatus status, String erro, String mensagem) {
        return ResponseEntity.status(status)
                .body(new ApiError(status.value(), erro, mensagem, LocalDateTime.now()));
    }
}
