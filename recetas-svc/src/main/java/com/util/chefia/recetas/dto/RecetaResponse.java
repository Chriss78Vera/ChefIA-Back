package com.util.chefia.recetas.dto;

import com.util.chefia.recetas.model.*;
import java.util.Set;

public record RecetaResponse(
    Long id,
    String nombre,
    String descripcion,
    Integer tiempoMinutos,
    Dificultad dificultad,
    TipoAlimentacion tipoAlimentacion,
    Set<String> ingredientes,
    Integer porciones,
    java.util.List<String> pasos,
    Set<String> tags,
    boolean publica,
    String usuarioSub,
    Animo animo,
    TipoReceta tipoReceta
) {}


