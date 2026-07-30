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
/** Unifica los errores del servicio para que el frontend reciba estados y mensajes consistentes. */
public class ApiExceptionHandler {
    /** Convierte duplicados de identidad en un conflicto HTTP 409. */
    @ExceptionHandler(UsuarioDuplicadoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    Map<String, Object> duplicado(UsuarioDuplicadoException ex) {
        return error(409, "Usuario duplicado", ex.getMessage());
    }

    /** Resume las validaciones fallidas de los DTO en una respuesta HTTP 400. */
    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, Object> validacion(WebExchangeBindException ex) {
        var messages = ex.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        return error(400, "Solicitud invalida", messages);
    }

    /** Oculta la causa interna y responde un mensaje genérico ante credenciales inválidas. */
    @ExceptionHandler(CredencialesInvalidasException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Map<String, Object> credenciales(CredencialesInvalidasException ex) {
        return error(401, "Credenciales invalidas", "Usuario o contrasenia incorrectos");
    }

    /** Conserva el estado y la razón de los rechazos deliberados por reglas de negocio. */
    @ExceptionHandler(ResponseStatusException.class)
    org.springframework.http.ResponseEntity<Map<String, Object>> estado(ResponseStatusException ex) {
        int status = ex.getStatusCode().value();
        return org.springframework.http.ResponseEntity.status(ex.getStatusCode())
                .body(error(status, "Solicitud rechazada", ex.getReason()));
    }

    /** Informa indisponibilidad cuando Keycloak o una operación interna no pueden completarse. */
    @ExceptionHandler({ IllegalStateException.class, WebClientResponseException.ServiceUnavailable.class })
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    Map<String, Object> keycloak(Exception ex) {
        return error(503, "Keycloak no disponible", ex.getMessage());
    }

    /** Construye el formato común de error utilizado por todos los handlers. */
    private Map<String, Object> error(int status, String name, Object message) {
        return Map.of("timestamp", Instant.now(), "status", status, "error", name, "message", message);
    }
}
