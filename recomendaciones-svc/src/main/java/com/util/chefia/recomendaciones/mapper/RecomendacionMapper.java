package com.util.chefia.recomendaciones.mapper;

import com.util.chefia.recomendaciones.client.RecetasClient;
import com.util.chefia.recomendaciones.dto.RecomendacionDto.Item;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class RecomendacionMapper {
    public Item desdeCandidata(RecetasClient.RecetaCandidata receta, String motivo) {
        return new Item(
            receta.nombre(),
            motivo,
            receta.descripcion(),
            receta.porciones()==null?2:receta.porciones(),
            receta.tiempoMinutos(),
            receta.dificultad(),
            new ArrayList<>(receta.ingredientes()),
            receta.pasos()==null||receta.pasos().isEmpty()?pasosDe(receta):receta.pasos(),
            receta.tags()==null||receta.tags().isEmpty()?tagsDe(receta):new ArrayList<>(receta.tags()), receta.id(), "PUBLICA", true,
            receta.tipoReceta()==null?com.util.chefia.recomendaciones.dto.RecomendacionDto.TipoReceta.PLATO_FUERTE:
             com.util.chefia.recomendaciones.dto.RecomendacionDto.TipoReceta.valueOf(receta.tipoReceta())
        );
    }

    private List<String> pasosDe(RecetasClient.RecetaCandidata receta) {
        return List.of(
            "Lavar y preparar todos los ingredientes.",
            "Cocinar los ingredientes principales hasta que esten tiernos.",
            "Integrar, ajustar la sazon y servir."
        );
    }

    private List<String> tagsDe(RecetasClient.RecetaCandidata receta) {
        LinkedHashSet<String> tags = new LinkedHashSet<>();
        tags.add(receta.tipoAlimentacion().toLowerCase(Locale.ROOT));
        tags.add(receta.dificultad().toLowerCase(Locale.ROOT));
        if (receta.tiempoMinutos() <= 30) {
            tags.add("rapida");
        }
        return new ArrayList<>(tags);
    }
}

