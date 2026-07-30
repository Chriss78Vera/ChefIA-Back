package com.util.chefia.auth.exception;

public class CambioContraseniaRequeridoException extends RuntimeException {
    public CambioContraseniaRequeridoException() {
        super("La cuenta utiliza una contrasenia temporal");
    }
}
