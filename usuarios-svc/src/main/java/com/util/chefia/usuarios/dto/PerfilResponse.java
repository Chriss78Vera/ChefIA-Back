package com.util.chefia.usuarios.dto;

import com.util.chefia.usuarios.model.TipoAlimentacion;
import java.time.Instant;
import java.util.Set;

public record PerfilResponse(
    String keycloakSub,
    String nombre,
    String email,
    TipoAlimentacion tipoAlimentacion,
    Set<String> restricciones,
    Set<String> ingredientesNoDeseados,
    Instant actualizadoEn
) {}


