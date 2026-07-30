package com.util.chefia.auth.dto;

public record UsuarioAdminResponse(
        String id,
        String username,
        String email,
        String nombre,
        String apellido,
        boolean activo) {
}
