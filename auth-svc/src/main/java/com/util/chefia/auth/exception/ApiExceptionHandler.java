package com.util.chefia.auth.exception;

import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(UsuarioDuplicadoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    Map<String, Object> duplicado(UsuarioDuplicadoException ex) {
        return error(409, "Usuario duplicado", ex.getMessage());
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, Object> validacion(WebExchangeBindException ex) {
        var messages = ex.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        return error(400, "Solicitud invalida", messages);
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Map<String, Object> credenciales(CredencialesInvalidasException ex) {
        return error(401, "Credenciales invalidas", "Usuario o contrasenia incorrectos");
    }

    @ExceptionHandler(ResponseStatusException.class)
    org.springframework.http.ResponseEntity<Map<String, Object>> estado(ResponseStatusException ex) {
        int status = ex.getStatusCode().value();
        return org.springframework.http.ResponseEntity.status(ex.getStatusCode())
                .body(error(status, "Solicitud rechazada", ex.getReason()));
    }

    @ExceptionHandler({ IllegalStateException.class, WebClientResponseException.ServiceUnavailable.class })
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    Map<String, Object> keycloak(Exception ex) {
        return error(503, "Keycloak no disponible", ex.getMessage());
    }

    private Map<String, Object> error(int status, String name, Object message) {
        return Map.of("timestamp", Instant.now(), "status", status, "error", name, "message", message);
    }
}
