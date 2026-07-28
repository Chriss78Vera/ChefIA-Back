package com.util.chefia.recomendaciones.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.util.chefia.recomendaciones.dto.RecomendacionDto.Animo;
import com.util.chefia.recomendaciones.dto.RecomendacionDto.Item;
import com.util.chefia.recomendaciones.model.RecomendacionCache;
import com.util.chefia.recomendaciones.repository.RecomendacionCacheRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecomendacionCacheServiceImplTest {
 @Mock
 private RecomendacionCacheRepository repository;

 @Test
 void guardaYRecuperaUnaRecomendacion() throws Exception {
  var mapper = new ObjectMapper().findAndRegisterModules();
  var service = new RecomendacionCacheServiceImpl(repository, mapper);
  var item = new Item("Sopa", "Calida", "Sopa vegetal", 2, 30, "FACIL",
   List.of("Lentejas"), List.of("Cocinar"), List.of("vegano"),null,"OLLAMA",false,
   com.util.chefia.recomendaciones.dto.RecomendacionDto.TipoReceta.SOPA);
  var tresItems = List.of(item, item, item);
  var json = mapper.writeValueAsString(tresItems);
  when(repository.findFirstByUsuarioSubAndAnimoAndTipoRecetaAndPreferenciaOrderByCreadoEnDesc("U123", "ESTRESADO", "SOPA", "VEGANO"))
   .thenReturn(Optional.of(new RecomendacionCache("U123", "ESTRESADO", com.util.chefia.recomendaciones.dto.RecomendacionDto.TipoReceta.SOPA, "VEGANO", json, Instant.now())));

  service.guardar("U123", Animo.ESTRESADO, com.util.chefia.recomendaciones.dto.RecomendacionDto.TipoReceta.SOPA, "VEGANO", tresItems);
  var resultado = service.buscarUltima("U123", Animo.ESTRESADO, com.util.chefia.recomendaciones.dto.RecomendacionDto.TipoReceta.SOPA, "VEGANO");

  verify(repository).save(any(RecomendacionCache.class));
  assertThat(resultado).isPresent();
  assertThat(resultado.orElseThrow()).containsExactlyElementsOf(tresItems);
 }
}
