package com.util.chefia.auth.dto;

/** Vista administrativa segura de una cuenta normal, sin credenciales ni roles internos. */
public record UsuarioAdminResponse(
        String id,
        String username,
        String email,
        String nombre,
        String apellido,
        boolean activo) {
}
