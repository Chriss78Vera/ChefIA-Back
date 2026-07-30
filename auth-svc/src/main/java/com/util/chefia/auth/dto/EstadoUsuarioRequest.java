package com.util.chefia.auth.dto;

import jakarta.validation.constraints.NotNull;

public record EstadoUsuarioRequest(@NotNull Boolean activo) {
}
