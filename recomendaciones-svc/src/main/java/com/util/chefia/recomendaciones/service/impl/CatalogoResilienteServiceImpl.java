package com.util.chefia.recomendaciones.service.impl;

import com.util.chefia.recomendaciones.client.RecetasClient;
import com.util.chefia.recomendaciones.service.CatalogoResilienteService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CatalogoResilienteServiceImpl implements CatalogoResilienteService {
 private static final Logger log=LoggerFactory.getLogger(CatalogoResilienteServiceImpl.class);
 private final RecetasClient client;

 public CatalogoResilienteServiceImpl(RecetasClient client){this.client=client;}

 @Override
 @CircuitBreaker(name="recetas",fallbackMethod="fallback")
 @Retry(name="recetas")
 @TimeLimiter(name="recetas")
 public CompletableFuture<List<RecetasClient.RecetaCandidata>> candidatas(String tipo,String authorization){
  return CompletableFuture.supplyAsync(()->client.listar(authorization,tipo));
 }

 private CompletableFuture<List<RecetasClient.RecetaCandidata>> fallback(String tipo,String authorization,Throwable error){
  log.warn("recetas-svc no esta disponible; se usara el catalogo seguro de respaldo. Causa: {}",error.toString());
  return CompletableFuture.completedFuture(List.of(
   new RecetasClient.RecetaCandidata(null,"Sopa de lentejas","Receta vegana de respaldo",30,"FACIL","VEGANO",Set.of("lentejas","zanahoria","cebolla")),
   new RecetasClient.RecetaCandidata(null,"Bowl de quinoa","Receta vegana de respaldo",25,"FACIL","VEGANO",Set.of("quinoa","garbanzos","aguacate")),
   new RecetasClient.RecetaCandidata(null,"Spaghetti con salsa de tomate","Receta vegana de respaldo",30,"FACIL","VEGANO",Set.of("pasta","tomate","albahaca"))));
 }
}
