package com.util.chefia.recomendaciones.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.util.chefia.recomendaciones.dto.RecomendacionDto.Animo;
import com.util.chefia.recomendaciones.dto.RecomendacionDto.Item;
import com.util.chefia.recomendaciones.dto.RecomendacionDto.HistorialResponse;
import com.util.chefia.recomendaciones.dto.RecomendacionDto.TipoReceta;
import com.util.chefia.recomendaciones.model.RecomendacionCache;
import com.util.chefia.recomendaciones.repository.RecomendacionCacheRepository;
import com.util.chefia.recomendaciones.service.RecomendacionCacheService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecomendacionCacheServiceImpl implements RecomendacionCacheService {
 private final RecomendacionCacheRepository repository;
 private final ObjectMapper objectMapper;

 public RecomendacionCacheServiceImpl(RecomendacionCacheRepository repository, ObjectMapper objectMapper) {
  this.repository = repository;
  this.objectMapper = objectMapper;
 }

 @Override
 @Transactional
 public void guardar(String usuarioSub, Animo animo, TipoReceta tipoReceta, String preferencia, List<Item> recomendaciones) {
  try {
   String json = objectMapper.writeValueAsString(recomendaciones);
   repository.save(new RecomendacionCache(usuarioSub, animo.name(), tipoReceta, preferencia, json, Instant.now()));
  } catch (JsonProcessingException error) {
   throw new IllegalStateException("No fue posible guardar las recomendaciones", error);
  }
 }

 @Override
 @Transactional(readOnly = true)
 public Optional<List<Item>> buscarUltima(String usuarioSub, Animo animo, TipoReceta tipoReceta, String preferencia) {
  return repository.findFirstByUsuarioSubAndAnimoAndTipoRecetaAndPreferenciaOrderByCreadoEnDesc(
   usuarioSub, animo.name(), tipoReceta.name(), preferencia)
   .map(this::leerRecomendaciones);
 }

 @Override
 @Transactional(readOnly = true)
 public List<HistorialResponse> listar(String usuarioSub) {
  return repository.findByUsuarioSubOrderByCreadoEnDesc(usuarioSub).stream()
   .map(cache -> new HistorialResponse(cache.getId(), Animo.valueOf(cache.getAnimo()),
    cache.getTipoReceta()==null?null:TipoReceta.valueOf(cache.getTipoReceta()), cache.getPreferencia(), leerRecomendaciones(cache), cache.getCreadoEn()))
   .toList();
 }

 private List<Item> leerRecomendaciones(RecomendacionCache cache) {
  try {
   var tipo = objectMapper.getTypeFactory().constructCollectionType(List.class, Item.class);
   List<Item> recomendaciones = objectMapper.readValue(cache.getRecomendacionesJson(), tipo);
   if (recomendaciones.size() != 3) {
    throw new IllegalStateException("La recomendacion guardada debe contener exactamente tres elementos");
   }
   return recomendaciones;
  } catch (JsonProcessingException error) {
   throw new IllegalStateException("Las recomendaciones guardadas no tienen un formato valido", error);
  }
 }
}
