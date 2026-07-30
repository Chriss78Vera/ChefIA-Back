package com.util.chefia.recomendaciones.dto;

import jakarta.validation.constraints.*;
import java.util.List;
import java.time.Instant;

public final class RecomendacionDto {
    private RecomendacionDto() {
    }

    public enum Animo {
        FELIZ, CANSADO, ESTRESADO, NOSTALGICO, AVENTURERO
    }

    public enum TipoReceta {
        DESAYUNO, ENTRANTE, SOPA, PLATO_FUERTE, POSTRE
    }

    public record Request(@NotNull Animo animo, @NotNull TipoReceta tipoReceta) {
    }

    public record Item(
            @NotBlank String nombre,
            @NotBlank String motivo,
            @NotBlank String descripcion,
            @Min(1) int porciones,
            @Min(1) int tiempoMinutos,
            @NotBlank String dificultad,
            @NotEmpty List<String> ingredientes,
            @NotEmpty List<String> pasos,
            @NotEmpty List<String> tags,
            Long recetaId,
            String origen,
            boolean isSaved,
            TipoReceta tipoReceta) {
    }

    public record Response(Animo animo, String preferencia, List<Item> recomendaciones, boolean fallback,
            String fuente) {
    }

    public record HistorialResponse(Long id, Animo animo, TipoReceta tipoReceta, String preferencia,
            List<Item> recomendaciones, Instant creadoEn) {
    }

    public record AiPayload(List<Item> recomendaciones) {
    }
}
