package com.util.chefia.recetas.mapper;

import com.util.chefia.recetas.dto.*;
import com.util.chefia.recetas.model.Receta;
import java.util.Set;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RecetaMapper {
    public void actualizarEntidad(RecetaRequest request, Receta receta) {
        receta.actualizar(
                request.nombre(),
                request.descripcion(),
                request.tiempoMinutos(),
                request.dificultad(),
                request.tipoAlimentacion(),
                request.ingredientes());
        receta.definirTipoReceta(request.tipoReceta());
    }

    public RecetaResponse aResponse(Receta receta) {
        return new RecetaResponse(
                receta.getId(),
                receta.getNombre(),
                receta.getDescripcion(),
                receta.getTiempoMinutos(),
                receta.getDificultad(),
                receta.getTipoAlimentacion(),
                Set.copyOf(receta.getIngredientes()), receta.getPorciones(), List.copyOf(receta.getPasos()),
                Set.copyOf(receta.getTags()), receta.isPublica(), receta.getUsuarioSub(), receta.getAnimo(),
                receta.getTipoReceta());
    }
}
