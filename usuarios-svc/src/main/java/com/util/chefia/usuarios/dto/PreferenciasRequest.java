package com.util.chefia.usuarios.dto;
import com.util.chefia.usuarios.model.TipoAlimentacion;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
public record PreferenciasRequest(
    @NotNull TipoAlimentacion tipoAlimentacion,
    Set<String> restricciones,
    Set<String> ingredientesNoDeseados
) {
    public PreferenciasRequest {
        restricciones = restricciones == null ? Set.of() : restricciones;
        ingredientesNoDeseados = ingredientesNoDeseados == null ? Set.of() : ingredientesNoDeseados;
    }
}


