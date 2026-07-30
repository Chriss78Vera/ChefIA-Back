package com.util.chefia.recomendaciones.service;

import com.util.chefia.recomendaciones.dto.RecomendacionDto.*;

public interface RecomendacionService {
    Response recomendar(Animo animo, TipoReceta tipoReceta, String usuarioSub, String accessToken);

    java.util.List<HistorialResponse> historial(String usuarioSub);
}
