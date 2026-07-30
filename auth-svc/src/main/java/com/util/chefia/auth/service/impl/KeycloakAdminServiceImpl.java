package com.util.chefia.auth.service.impl;

import com.util.chefia.auth.dto.CrearUsuarioRequest;
import com.util.chefia.auth.dto.UsuarioCreadoResponse;
import com.util.chefia.auth.dto.UsuarioAdminResponse;
import com.util.chefia.auth.exception.UsuarioDuplicadoException;
import com.util.chefia.auth.mapper.KeycloakMapper;
import com.util.chefia.auth.service.KeycloakAdminService;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import reactor.core.publisher.Flux;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.FORBIDDEN;
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
/** Centraliza las operaciones sobre usuarios y roles mediante la API administrativa de Keycloak. */
public class KeycloakAdminServiceImpl implements KeycloakAdminService {
    private final WebClient keycloak;
    private final KeycloakMapper mapper;
    private final String realm;
    private final String adminUsername;
    private final String adminPassword;

    public KeycloakAdminServiceImpl(
            WebClient.Builder builder,
            KeycloakMapper mapper,
            @Value("${keycloak.internal-url}") String baseUrl,
            @Value("${keycloak.realm}") String realm,
            @Value("${keycloak.admin-username}") String adminUsername,
            @Value("${keycloak.admin-password}") String adminPassword) {
        this.keycloak = builder.baseUrl(baseUrl).build();
        this.mapper = mapper;
        this.realm = realm;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    /** Crea una cuenta pública con contraseña definitiva. */
    public Mono<UsuarioCreadoResponse> crear(CrearUsuarioRequest request) {
        return crear(request, false);
    }

    @Override
    /** Crea desde administración una cuenta obligada a cambiar su contraseña temporal. */
    public Mono<UsuarioCreadoResponse> crearTemporal(CrearUsuarioRequest request) {
        return crear(request, true);
    }

    /** Comparte el alta, asigna USUARIO y traduce conflictos de username o correo. */
    private Mono<UsuarioCreadoResponse> crear(CrearUsuarioRequest request, boolean temporal) {
        return tokenAdmin()
                .flatMap(token -> keycloak.post()
                        .uri("/admin/realms/{realm}/users", realm)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(mapper.aUsuario(request, temporal))
                        .retrieve()
                        .toBodilessEntity()
                        .map(response -> extraerId(response.getHeaders().getLocation()))
                        .flatMap(id -> asignarRolUsuario(token, id).thenReturn(id)))
                .map(id -> new UsuarioCreadoResponse(id, request.username(), request.email(),
                        request.nombre(), request.apellido(), "USUARIO", temporal))
                .onErrorMap(WebClientResponseException.Conflict.class,
                        error -> new UsuarioDuplicadoException("El username o email ya esta registrado"));
    }

    @Override
    /** Consulta las identidades y descarta las que tengan el rol ADMIN. */
    public Mono<List<UsuarioAdminResponse>> listarUsuariosNormales() {
        return tokenAdmin().flatMapMany(token -> usuarios(token)
                .flatMapMany(Flux::fromIterable)
                .flatMap(usuario -> roles(token, id(usuario))
                        .filter(roles -> !esAdmin(roles))
                        .map(roles -> aResponse(usuario))))
                .collectList();
    }

    @Override
    /** Cambia enabled después de comprobar que el objetivo no sea administrador. */
    public Mono<UsuarioAdminResponse> cambiarEstado(String userId, boolean activo) {
        return tokenAdmin().flatMap(token -> usuario(token, userId)
                .zipWith(roles(token, userId))
                .flatMap(datos -> {
                    if (esAdmin(datos.getT2())) {
                        return Mono.error(new ResponseStatusException(FORBIDDEN,
                                "No se puede cambiar el estado de un administrador"));
                    }
                    Map<String, Object> actualizado = new LinkedHashMap<>(datos.getT1());
                    actualizado.put("enabled", activo);
                    return keycloak.put().uri("/admin/realms/{realm}/users/{id}", realm, userId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON).bodyValue(actualizado)
                            .retrieve().toBodilessEntity().thenReturn(aResponse(actualizado));
                }));
    }

    /** Obtiene todas las representaciones de usuario del realm. */
    private Mono<List<Map<String, Object>>> usuarios(String token) {
        return keycloak.get().uri("/admin/realms/{realm}/users", realm)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token).retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                });
    }

    /** Obtiene una identidad concreta por su identificador interno de Keycloak. */
    private Mono<Map<String, Object>> usuario(String token, String id) {
        return keycloak.get().uri("/admin/realms/{realm}/users/{id}", realm, id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token).retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                });
    }

    /** Consulta los roles de realm asignados directamente a una identidad. */
    private Mono<List<Map<String, Object>>> roles(String token, String id) {
        return keycloak.get().uri("/admin/realms/{realm}/users/{id}/role-mappings/realm", realm, id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token).retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                });
    }

    /** Determina si la colección contiene el rol protegido ADMIN. */
    private boolean esAdmin(List<Map<String, Object>> roles) {
        return roles.stream().anyMatch(role -> "ADMIN".equals(String.valueOf(role.get("name"))));
    }

    /** Proyecta únicamente los datos seguros requeridos por la pantalla administrativa. */
    private UsuarioAdminResponse aResponse(Map<String, Object> usuario) {
        return new UsuarioAdminResponse(id(usuario), texto(usuario, "username"), texto(usuario, "email"),
                texto(usuario, "firstName"), texto(usuario, "lastName"),
                Boolean.TRUE.equals(usuario.get("enabled")));
    }

    /** Extrae el identificador de una representación de usuario. */
    private String id(Map<String, Object> usuario) {
        return texto(usuario, "id");
    }

    /** Convierte campos opcionales a texto vacío para evitar valores nulos en la API. */
    private String texto(Map<String, Object> usuario, String campo) {
        Object value = usuario.get(campo);
        return value == null ? "" : String.valueOf(value);
    }

    /** Recupera la definición de USUARIO y la asigna a la cuenta recién creada. */
    private Mono<Void> asignarRolUsuario(String token, String userId) {
        return keycloak.get()
                .uri("/admin/realms/{realm}/roles/USUARIO", realm)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .flatMap(role -> keycloak.post()
                        .uri("/admin/realms/{realm}/users/{id}/role-mappings/realm", realm, userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(List.of(role))
                        .retrieve()
                        .toBodilessEntity())
                .then();
    }

    /** Solicita a master un token de admin-cli usado solo dentro del microservicio. */
    private Mono<String> tokenAdmin() {
        var form = new LinkedMultiValueMap<String, String>();
        form.add("client_id", "admin-cli");
        form.add("grant_type", "password");
        form.add("username", adminUsername);
        form.add("password", adminPassword);
        return keycloak.post()
                .uri("/realms/master/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(form)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .map(response -> String.valueOf(response.get("access_token")));
    }

    /** Obtiene el ID creado desde la cabecera Location devuelta por Keycloak. */
    private String extraerId(URI location) {
        if (location == null) {
            throw new IllegalStateException("Keycloak no devolvio el identificador del usuario");
        }
        return location.getPath().substring(location.getPath().lastIndexOf('/') + 1);
    }
}
