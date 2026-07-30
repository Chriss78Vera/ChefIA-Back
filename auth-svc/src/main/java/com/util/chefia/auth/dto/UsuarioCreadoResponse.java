package com.util.chefia.auth.dto;

public record UsuarioCreadoResponse(
        String id,
        String username,
        String email,
        String nombre,
        String apellido,
        String rol,
        boolean requiereCambioContrasenia) {
}
