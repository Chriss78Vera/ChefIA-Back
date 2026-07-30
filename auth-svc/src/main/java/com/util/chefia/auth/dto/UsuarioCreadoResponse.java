package com.util.chefia.auth.dto;

/** Confirma la cuenta creada e informa si debe reemplazar una contraseña temporal. */
public record UsuarioCreadoResponse(
        String id,
        String username,
        String email,
        String nombre,
        String apellido,
        String rol,
        boolean requiereCambioContrasenia) {
}
