package com.util.chefia.auth.dto;

import jakarta.validation.constraints.NotBlank;

/** Credenciales recibidas para solicitar tokens al endpoint OIDC de Keycloak. */
public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password) {
}
