package com.util.chefia.recomendaciones.service.impl;

import com.util.chefia.recomendaciones.client.*;
import com.util.chefia.recomendaciones.mapper.RecomendacionMapper;
import com.util.chefia.recomendaciones.dto.RecomendacionDto.Animo;
import com.util.chefia.recomendaciones.dto.RecomendacionDto.Response;
import com.util.chefia.recomendaciones.dto.RecomendacionDto.HistorialResponse;
import com.util.chefia.recomendaciones.dto.RecomendacionDto.TipoReceta;
import java.util.List;
import java.util.Set;
import com.util.chefia.recomendaciones.service.CatalogoResilienteService;
import com.util.chefia.recomendaciones.service.OllamaService;
import com.util.chefia.recomendaciones.service.RecomendacionCacheService;
import com.util.chefia.recomendaciones.service.RecomendacionService;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class RecomendacionServiceImpl implements RecomendacionService {
  private final UsuariosClient usuarios;
  private final CatalogoResilienteService catalogo;
  private final OllamaService ollama;
  private final RecomendacionCacheService cache;
  private final RecetasClient recetas;
  private final FavoritosClient favoritos;
  private final RecomendacionMapper mapper;

  public RecomendacionServiceImpl(UsuariosClient usuarios, CatalogoResilienteService catalogo,
      OllamaService ollama, RecomendacionCacheService cache, RecetasClient recetas,
      FavoritosClient favoritos, RecomendacionMapper mapper) {
    this.usuarios = usuarios;
    this.catalogo = catalogo;
    this.ollama = ollama;
    this.cache = cache;
    this.recetas = recetas;
    this.favoritos = favoritos;
    this.mapper = mapper;
  }

  @Override
  public Response recomendar(Animo animo, TipoReceta tipoReceta, String usuarioSub, String accessToken) {
    var pref = usuarios.obtener(accessToken);
    final var candidatas = obtenerCandidatas(tipoReceta, pref.tipoAlimentacion(), accessToken);
    final var publicas = obtenerPublicas(animo, tipoReceta, pref.tipoAlimentacion(), accessToken);
    final java.util.List<com.util.chefia.recomendaciones.dto.RecomendacionDto.Item> items;
    try {
      int cantidadOllama = publicas.isEmpty() ? 3 : 2;
      items = ollama.recomendar(animo, tipoReceta, pref, candidatas, cantidadOllama);
    } catch (Exception ollamaError) {
      if (publicas.size() >= 2) {
        var respaldo = publicas.stream().limit(3).map(r -> mapper.desdeCandidata(r, "Compatible con tu animo"))
            .toList();
        return new Response(animo, pref.tipoAlimentacion(), respaldo, true, "RECETAS_PUBLICAS");
      }
      return cache.buscarUltima(usuarioSub, animo, tipoReceta, pref.tipoAlimentacion())
          .map(cachedItems -> new Response(animo, pref.tipoAlimentacion(), cachedItems, true, "BASE_DATOS"))
          .orElseThrow(() -> new IllegalStateException(
              "Ollama no respondio y no existen recomendaciones guardadas", ollamaError));
    }
    var respuesta = new java.util.ArrayList<>(items);
    if (!publicas.isEmpty())
      respuesta.add(mapper.desdeCandidata(publicas.get(0), "Receta publica compatible con tu animo"));
    cache.guardar(usuarioSub, animo, tipoReceta, pref.tipoAlimentacion(), respuesta);
    boolean catalogFallback = candidatas.stream().anyMatch(recipe -> recipe.id() == null);
    return new Response(animo, pref.tipoAlimentacion(), respuesta, catalogFallback, "OLLAMA");
  }

  @Override
  public List<HistorialResponse> historial(String usuarioSub) {
    return cache.listar(usuarioSub);
  }

  private java.util.List<com.util.chefia.recomendaciones.client.RecetasClient.RecetaCandidata> obtenerCandidatas(
      TipoReceta tipoReceta, String preferencia, String accessToken) {
    try {
      return catalogo.candidatas(preferencia, "Bearer " + accessToken).get(7, TimeUnit.SECONDS).stream()
          .filter(receta -> receta.tipoReceta() != null && receta.tipoReceta().equals(tipoReceta.name())).toList();
    } catch (Exception error) {
      throw new IllegalStateException("No fue posible consultar el catalogo de recetas", error);
    }
  }

  private List<RecetasClient.RecetaCandidata> obtenerPublicas(Animo animo, TipoReceta tipoReceta, String tipo,
      String token) {
    try {
      Set<Long> guardadas = favoritos.ids(token);
      var lista = recetas.publicas("Bearer " + token, tipo, animo.name(), tipoReceta.name());
      return lista == null ? List.of() : lista.stream().filter(r -> !guardadas.contains(r.id())).toList();
    } catch (Exception error) {
      return List.of();
    }
  }
}
