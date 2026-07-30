package com.util.chefia.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Contrato para cambiar la clave de una sesión activa verificando clave actual y confirmación. */
public record CambioContraseniaRequest(
        @NotBlank @Size(min = 8, max = 100) String contraseniaActual,
        @NotBlank @Size(min = 8, max = 100) String contraseniaNueva,
        @NotBlank @Size(min = 8, max = 100) String confirmacionContrasenia) {
}
