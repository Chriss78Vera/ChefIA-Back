package com.util.chefia.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Datos permitidos al registrar una cuenta; el rol nunca se acepta desde el cliente. */
public record CrearUsuarioRequest(
        @NotBlank @Pattern(regexp = "^[a-zA-Z0-9._-]{3,50}$") String username,
        @NotBlank @Email String email,
        @NotBlank @Size(max = 80) String nombre,
        @NotBlank @Size(max = 80) String apellido,
        @NotBlank @Size(min = 8, max = 100) String password) {
}
