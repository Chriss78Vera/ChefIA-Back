package com.util.chefia.favoritos.client;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

@Component
public class RecetasClient {
 private static final Logger log=LoggerFactory.getLogger(RecetasClient.class);
 private final WebClient client;
 public RecetasClient(WebClient.Builder builder,@Value("${services.recetas.url}")String baseUrl){
  this.client=builder.baseUrl(baseUrl).build();
 }
 @CircuitBreaker(name="recetasFavoritos",fallbackMethod="fallbackObtener")
 @Retry(name="recetasFavoritos")
 public RecetaResumen obtener(Long id,String token){
  return client.get().uri("/api/recetas/{id}",id)
   .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
   .retrieve().bodyToMono(RecetaResumen.class).block();
 }
 private RecetaResumen fallbackObtener(Long id,String token,Throwable error){
  if(error instanceof WebClientResponseException response && response.getStatusCode().is4xxClientError()){
   throw new ResponseStatusException(HttpStatus.valueOf(response.getStatusCode().value()),"No fue posible consultar la receta",error);
  }
  log.warn("recetas-svc no esta disponible; se guardara el favorito {} con nombre provisional. Causa: {}",id,error.toString());
  return new RecetaResumen(id,"Receta "+id+" (servicio no disponible)",null,null,null,null);
 }
 public record RecetaResumen(Long id,String nombre,String descripcion,Integer tiempoMinutos,String dificultad,String tipoAlimentacion){}
}

