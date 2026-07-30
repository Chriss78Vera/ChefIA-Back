package com.util.chefia.recomendaciones.service;

import com.util.chefia.recomendaciones.client.RecetasClient;
import com.util.chefia.recomendaciones.client.UsuariosClient;
import com.util.chefia.recomendaciones.dto.RecomendacionDto.Animo;
import com.util.chefia.recomendaciones.dto.RecomendacionDto.Item;
import com.util.chefia.recomendaciones.dto.RecomendacionDto.TipoReceta;
import java.util.List;

public interface OllamaService {
    List<Item> recomendar(Animo animo, TipoReceta tipoReceta, UsuariosClient.Preferencias pref,
            List<RecetasClient.RecetaCandidata> recetas, int cantidad);
}
