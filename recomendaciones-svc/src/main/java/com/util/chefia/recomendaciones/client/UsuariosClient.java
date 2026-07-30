package com.util.chefia.recomendaciones.client;

import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class UsuariosClient {
  private final WebClient client;

  public UsuariosClient(WebClient.Builder builder, @Value("${services.usuarios.url}") String baseUrl) {
    this.client = builder.baseUrl(baseUrl).build();
  }

  public Preferencias obtener(String token) {
    return client.get().uri("/api/usuarios/preferencias")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .retrieve().bodyToMono(Preferencias.class).block();
  }

  public record Preferencias(String keycloakSub, String tipoAlimentacion, Set<String> restricciones,
      Set<String> ingredientesNoDeseados) {
  }
}
