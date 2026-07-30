package com.util.chefia.auth.dto;

import jakarta.validation.constraints.NotNull;

/** Estado enabled que un administrador desea persistir en Keycloak. */
public record EstadoUsuarioRequest(@NotNull Boolean activo) {
}
