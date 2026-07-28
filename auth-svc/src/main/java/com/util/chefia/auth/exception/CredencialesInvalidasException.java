package com.util.chefia.auth.exception;

public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException() {
        super("Usuario o contrasenia incorrectos");
    }
}
