package com.util.chefia.favoritos.dto;

import java.time.Instant;

public record FavoritoResponse(
        Long id,
        Long recetaId,
        String recetaNombre,
        Instant creadoEn) {
}
