package com.util.chefia.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CambioContraseniaTemporalRequest(
    @NotBlank @Pattern(regexp = "^[a-zA-Z0-9._-]{3,50}$") String username,
    @NotBlank @Size(min = 8, max = 100) String contraseniaTemporal,
    @NotBlank @Size(min = 8, max = 100) String contraseniaNueva,
    @NotBlank @Size(min = 8, max = 100) String confirmacionContrasenia
) {}
