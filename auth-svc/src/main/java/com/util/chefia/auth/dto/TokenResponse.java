package com.util.chefia.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Tokens y tiempos de vigencia devueltos por Keycloak después de autenticar. */
public record TokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("expires_in") long expiresIn,
        @JsonProperty("refresh_expires_in") long refreshExpiresIn,
        @JsonProperty("token_type") String tokenType) {
}
