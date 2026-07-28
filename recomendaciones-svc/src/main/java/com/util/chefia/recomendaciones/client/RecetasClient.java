package com.util.chefia.recomendaciones.client;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class RecetasClient {
 private final WebClient client;
 public RecetasClient(WebClient.Builder builder,@Value("${services.recetas.url}")String baseUrl){
  this.client=builder.baseUrl(baseUrl).build();
 }
 public List<RecetaCandidata> listar(String authorization,String tipoAlimentacion){
  return client.get().uri(builder->builder.path("/api/recetas")
    .queryParamIfPresent("tipoAlimentacion",Optional.ofNullable(tipoAlimentacion)).build())
   .header(HttpHeaders.AUTHORIZATION,authorization)
   .retrieve().bodyToMono(new ParameterizedTypeReference<List<RecetaCandidata>>(){}).block();
 }
 public List<RecetaCandidata> publicas(String authorization,String tipoAlimentacion,String animo,String tipoReceta){
  return client.get().uri(builder->builder.path("/api/recetas/publicas")
    .queryParam("tipoAlimentacion",tipoAlimentacion).queryParam("animo",animo).queryParam("tipoReceta",tipoReceta).build())
   .header(HttpHeaders.AUTHORIZATION,authorization).retrieve()
   .bodyToMono(new ParameterizedTypeReference<List<RecetaCandidata>>(){}).block();
 }
 public record RecetaCandidata(Long id,String nombre,String descripcion,Integer tiempoMinutos,String dificultad,String tipoAlimentacion,Set<String> ingredientes,Integer porciones,List<String> pasos,Set<String> tags,Boolean publica,String usuarioSub,String animo,String tipoReceta){
  public RecetaCandidata(Long id,String nombre,String descripcion,Integer tiempoMinutos,String dificultad,String tipoAlimentacion,Set<String> ingredientes){
   this(id,nombre,descripcion,tiempoMinutos,dificultad,tipoAlimentacion,ingredientes,null,null,null,null,null,null,null);
  }
 }
}

