package com.util.chefia.favoritos.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class RecetasClient {
  private final WebClient client;

  public RecetasClient(WebClient.Builder builder, @Value("${services.recetas.url}") String baseUrl) {
    this.client = builder.baseUrl(baseUrl).build();
  }

  @CircuitBreaker(name = "recetasFavoritos", fallbackMethod = "fallbackObtener")
  @Retry(name = "recetasFavoritos")
  public RecetaResumen obtener(Long id, String token) {
    return client.get().uri("/api/recetas/{id}", id)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .retrieve().bodyToMono(RecetaResumen.class).block();
  }

  public record RecetaResumen(Long id, String nombre, String descripcion, Integer tiempoMinutos, String dificultad,
      String tipoAlimentacion) {
  }
}
