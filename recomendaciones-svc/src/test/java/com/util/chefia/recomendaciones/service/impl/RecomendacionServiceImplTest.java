package com.util.chefia.recomendaciones.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.util.chefia.recomendaciones.client.RecetasClient.RecetaCandidata;
import com.util.chefia.recomendaciones.client.UsuariosClient;
import com.util.chefia.recomendaciones.client.RecetasClient;
import com.util.chefia.recomendaciones.client.FavoritosClient;
import com.util.chefia.recomendaciones.mapper.RecomendacionMapper;
import com.util.chefia.recomendaciones.client.UsuariosClient.Preferencias;
import com.util.chefia.recomendaciones.dto.RecomendacionDto.Animo;
import com.util.chefia.recomendaciones.dto.RecomendacionDto.Item;
import com.util.chefia.recomendaciones.dto.RecomendacionDto.TipoReceta;
import com.util.chefia.recomendaciones.service.CatalogoResilienteService;
import com.util.chefia.recomendaciones.service.OllamaService;
import com.util.chefia.recomendaciones.service.RecomendacionCacheService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;

class RecomendacionServiceImplTest {
 @Test
 void usaBaseDeDatosCuandoOllamaFalla() {
  var usuarios = mock(UsuariosClient.class);
  var catalogo = mock(CatalogoResilienteService.class);
  var ollama = mock(OllamaService.class);
  var cache = mock(RecomendacionCacheService.class);
  var recetas = mock(RecetasClient.class);
  var favoritos = mock(FavoritosClient.class);
  var mapper = mock(RecomendacionMapper.class);
  var preferencias = new Preferencias("sub", "VEGANO", Set.of(), Set.of());
  var candidata = new RecetaCandidata(1L, "Sopa", "Calida", 30, "FACIL", "VEGANO", Set.of("Lentejas"));
  var guardada = new Item("Sopa", "Calida", "Sopa vegetal", 2, 30, "FACIL",
   List.of("Lentejas"), List.of("Cocinar"), List.of("vegano"),null,"OLLAMA",false,TipoReceta.SOPA);
  when(usuarios.obtener("token")).thenReturn(preferencias);
  when(catalogo.candidatas("VEGANO", "Bearer token"))
   .thenReturn(CompletableFuture.completedFuture(List.of(candidata)));
  when(ollama.recomendar(Animo.ESTRESADO, TipoReceta.SOPA, preferencias, List.of(),3))
   .thenThrow(new IllegalStateException("Ollama no responde"));
  when(cache.buscarUltima("U123", Animo.ESTRESADO, TipoReceta.SOPA, "VEGANO")).thenReturn(Optional.of(List.of(guardada)));
  when(favoritos.ids("token")).thenReturn(Set.of());
  when(recetas.publicas("Bearer token","VEGANO","ESTRESADO","SOPA")).thenReturn(List.of());
  var service = new RecomendacionServiceImpl(usuarios, catalogo, ollama, cache, recetas, favoritos, mapper);

  var respuesta = service.recomendar(Animo.ESTRESADO, TipoReceta.SOPA, "U123", "token");

  assertThat(respuesta.fuente()).isEqualTo("BASE_DATOS");
  assertThat(respuesta.fallback()).isTrue();
  assertThat(respuesta.recomendaciones()).containsExactly(guardada);
 }
}
