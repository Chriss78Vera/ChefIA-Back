package com.util.chefia.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
/** Expone el constructor compartido para los clientes HTTP reactivos que se comunican con Keycloak. */
public class WebClientConfig {
    /** Crea builders independientes que cada servicio configura con su propia URL base. */
    @Bean
    WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
