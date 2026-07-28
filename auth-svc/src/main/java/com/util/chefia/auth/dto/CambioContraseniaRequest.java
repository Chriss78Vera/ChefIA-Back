package com.util.chefia.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CambioContraseniaRequest(
    @NotBlank @Size(min = 8, max = 100) String contraseniaActual,
    @NotBlank @Size(min = 8, max = 100) String contraseniaNueva,
    @NotBlank @Size(min = 8, max = 100) String confirmacionContrasenia
) {}
