package com.util.chefia.recomendaciones.service;

import com.util.chefia.recomendaciones.dto.RecomendacionDto.Animo;
import com.util.chefia.recomendaciones.dto.RecomendacionDto.Item;
import com.util.chefia.recomendaciones.dto.RecomendacionDto.HistorialResponse;
import com.util.chefia.recomendaciones.dto.RecomendacionDto.TipoReceta;
import java.util.List;
import java.util.Optional;

public interface RecomendacionCacheService {
 void guardar(String usuarioSub, Animo animo, TipoReceta tipoReceta, String preferencia, List<Item> recomendaciones);
 Optional<List<Item>> buscarUltima(String usuarioSub, Animo animo, TipoReceta tipoReceta, String preferencia);
 List<HistorialResponse> listar(String usuarioSub);
}
