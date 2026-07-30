package com.util.chefia.auth.exception;

/** Representa el conflicto producido cuando username o correo ya existen en el realm. */
public class UsuarioDuplicadoException extends RuntimeException {
    public UsuarioDuplicadoException(String message) {
        super(message);
    }
}
