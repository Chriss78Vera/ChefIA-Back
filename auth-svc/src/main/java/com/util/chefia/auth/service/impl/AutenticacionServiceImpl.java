package com.util.chefia.auth.service.impl;

import com.util.chefia.auth.dto.LoginRequest;
import com.util.chefia.auth.dto.TokenResponse;
import com.util.chefia.auth.mapper.KeycloakMapper;
import com.util.chefia.auth.exception.CredencialesInvalidasException;
import com.util.chefia.auth.service.AutenticacionService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class AutenticacionServiceImpl implements AutenticacionService {
    private final WebClient keycloak;
    private final KeycloakMapper mapper;
    private final String realm;
    private final String clientId;
    private final String publicHost;

    public AutenticacionServiceImpl(
            WebClient.Builder builder,
            KeycloakMapper mapper,
            @Value("${keycloak.public-url}") String baseUrl,
            @Value("${keycloak.public-host}") String publicHost,
            @Value("${keycloak.realm}") String realm,
            @Value("${keycloak.public-client-id}") String clientId) {
        this.keycloak = builder.baseUrl(baseUrl).build();
        this.mapper = mapper;
        this.publicHost = publicHost;
        this.realm = realm;
        this.clientId = clientId;
    }

    @Override
    public Mono<TokenResponse> login(LoginRequest request) {
        var form = new LinkedMultiValueMap<String, String>();
        form.add("grant_type", "password");
        form.add("client_id", clientId);
        form.add("username", request.username());
        form.add("password", request.password());
        return keycloak.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", realm)
                .header(HttpHeaders.HOST, publicHost)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(form)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .map(mapper::aToken)
                .onErrorMap(WebClientResponseException.BadRequest.class,
                        error -> new CredencialesInvalidasException())
                .onErrorMap(WebClientResponseException.Unauthorized.class,
                        error -> new CredencialesInvalidasException());
    }
}
