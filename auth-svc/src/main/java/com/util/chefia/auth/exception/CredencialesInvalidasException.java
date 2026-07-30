package com.util.chefia.auth.exception;

/** Señala credenciales rechazadas sin exponer detalles sensibles entregados por Keycloak. */
public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException() {
        super("Usuario o contrasenia incorrectos");
    }
}
