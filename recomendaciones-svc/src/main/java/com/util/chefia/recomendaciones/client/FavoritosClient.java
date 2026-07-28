package com.util.chefia.recomendaciones.client;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
@Component
public class FavoritosClient {
 private final WebClient client;
 public FavoritosClient(WebClient.Builder builder,@Value("${services.favoritos.url}")String url){client=builder.baseUrl(url).build();}
 public Set<Long> ids(String token){
  List<Favorito> lista=client.get().uri("/api/favoritos").header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
   .retrieve().bodyToMono(new ParameterizedTypeReference<List<Favorito>>(){}).block();
  if(lista==null)return Set.of();
  Set<Long> ids=new HashSet<>();lista.forEach(f->ids.add(f.recetaId()));return ids;
 }
 public record Favorito(Long id,Long recetaId,String recetaNombre,String creadoEn){}
}
