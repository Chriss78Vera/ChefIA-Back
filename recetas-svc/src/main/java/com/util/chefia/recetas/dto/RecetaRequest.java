package com.util.chefia.recetas.dto;
import com.util.chefia.recetas.model.*;
import jakarta.validation.constraints.*;
import java.util.Set;
public record RecetaRequest(
 @NotBlank String nombre, @NotBlank String descripcion,
 @NotNull @Min(1) @Max(1440) Integer tiempoMinutos,
 @NotNull Dificultad dificultad, @NotNull TipoAlimentacion tipoAlimentacion, @NotNull TipoReceta tipoReceta,
 @NotEmpty Set<@NotBlank String> ingredientes) {}


