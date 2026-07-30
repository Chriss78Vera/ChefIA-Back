package com.util.chefia.recetas.dto;

import com.util.chefia.recetas.model.*;
import jakarta.validation.constraints.*;
import java.util.*;

public record RecetaUsuarioRequest(
        @NotBlank String nombre, @NotBlank String descripcion, @Min(1) int porciones,
        @Min(1) @Max(1440) int tiempoMinutos, @NotNull Dificultad dificultad,
        @NotNull TipoAlimentacion tipoAlimentacion, @NotNull Animo animo, @NotNull TipoReceta tipoReceta,
        boolean publica,
        @NotEmpty Set<@NotBlank String> ingredientes, @NotEmpty List<@NotBlank String> pasos,
        @NotEmpty Set<@NotBlank String> tags) {
}
