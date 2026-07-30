package com.util.chefia.auth.service.impl;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.util.chefia.auth.dto.CambioContraseniaRequest;
import com.util.chefia.auth.dto.CambioContraseniaTemporalRequest;
import com.util.chefia.auth.dto.MensajeResponse;
import com.util.chefia.auth.service.ContraseniaService;
import com.util.chefia.auth.exception.CredencialesInvalidasException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
/** Gestiona credenciales en Keycloak sin almacenar contraseñas en bases propias. */
public class ContraseniaServiceImpl implements ContraseniaService {
    private static final String UPDATE_PASSWORD = "UPDATE_PASSWORD";

    private final WebClient keycloakAdmin;
    private final WebClient keycloakPublico;
    private final String publicHost;
    private final String realm;
    private final String clientId;
    private final String adminUsername;
    private final String adminPassword;

    public ContraseniaServiceImpl(
            WebClient.Builder builder,
            @Value("${keycloak.internal-url}") String internalUrl,
            @Value("${keycloak.public-url}") String publicUrl,
            @Value("${keycloak.public-host}") String publicHost,
            @Value("${keycloak.realm}") String realm,
            @Value("${keycloak.public-client-id}") String clientId,
            @Value("${keycloak.admin-username}") String adminUsername,
            @Value("${keycloak.admin-password}") String adminPassword) {
        this.keycloakAdmin = builder.clone().baseUrl(internalUrl).build();
        this.keycloakPublico = builder.clone().baseUrl(publicUrl).build();
        this.publicHost = publicHost;
        this.realm = realm;
        this.clientId = clientId;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    /** Verifica confirmación, diferencia y clave actual antes de registrar una credencial definitiva. */
    public Mono<MensajeResponse> cambiar(
            String userId, String username, CambioContraseniaRequest request) {
        validarConfirmacion(request.contraseniaNueva(), request.confirmacionContrasenia());
        validarDiferente(request.contraseniaActual(), request.contraseniaNueva());
        return validarCredenciales(username, request.contraseniaActual())
                .then(tokenAdmin())
                .flatMap(token -> cambiarCredencial(token, userId, request.contraseniaNueva()))
                .thenReturn(new MensajeResponse("Contrasenia actualizada correctamente"));
    }

    @Override
    /** Sustituye una clave temporal únicamente cuando la cuenta conserva UPDATE_PASSWORD. */
    public Mono<MensajeResponse> cambiarTemporal(CambioContraseniaTemporalRequest request) {
        validarConfirmacion(request.contraseniaNueva(), request.confirmacionContrasenia());
        validarDiferente(request.contraseniaTemporal(), request.contraseniaNueva());

        return tokenAdmin().flatMap(token -> buscarPorUsername(token, request.username())
                .flatMap(usuario -> {
                    String userId = texto(usuario, "id");
                    if (!Boolean.TRUE.equals(usuario.get("enabled"))) {
                        return Mono.error(new ResponseStatusException(CONFLICT, "El usuario esta inactivo"));
                    }
                    if (!acciones(usuario).contains(UPDATE_PASSWORD)) {
                        return Mono.error(new ResponseStatusException(CONFLICT,
                                "El usuario no tiene un cambio de contrasenia temporal pendiente"));
                    }

                    // Keycloak bloquea el password grant mientras UPDATE_PASSWORD sigue pendiente.
                    Map<String, Object> sinAccion = conAccionCambio(usuario, false);
                    return actualizarUsuario(token, userId, sinAccion)
                            .then(validarCredenciales(request.username(), request.contraseniaTemporal()))
                            .then(cambiarCredencial(token, userId, request.contraseniaNueva()))
                            .thenReturn(new MensajeResponse("Contrasenia temporal actualizada correctamente"))
                            // Si falla la comprobación o el cambio, se intenta restaurar la acción original.
                            .onErrorResume(error -> actualizarUsuario(token, userId, usuario)
                                    .onErrorResume(ignorado -> Mono.empty())
                                    .then(Mono.error(error)));
                }));
    }

    /** Busca una coincidencia exacta para evitar operar sobre usuarios con nombres parecidos. */
    private Mono<Map<String, Object>> buscarPorUsername(String token, String username) {
        return keycloakAdmin.get()
                .uri(uri -> uri.path("/admin/realms/{realm}/users")
                        .queryParam("username", username).queryParam("exact", true).build(realm))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                })
                .flatMap(usuarios -> usuarios.size() == 1
                        ? Mono.just(usuarios.get(0))
                        : Mono.error(new ResponseStatusException(NOT_FOUND, "Usuario no encontrado")));
    }

    /** Comprueba una contraseña mediante el endpoint OIDC sin conservar el token obtenido. */
    private Mono<Void> validarCredenciales(String username, String password) {
        var form = new LinkedMultiValueMap<String, String>();
        form.add("grant_type", "password");
        form.add("client_id", clientId);
        form.add("username", username);
        form.add("password", password);
        return keycloakPublico.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", realm)
                .header(HttpHeaders.HOST, publicHost)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(form)
                .retrieve()
                .toBodilessEntity()
                .then()
                .onErrorMap(WebClientResponseException.BadRequest.class,
                        error -> new CredencialesInvalidasException())
                .onErrorMap(WebClientResponseException.Unauthorized.class,
                        error -> new CredencialesInvalidasException());
    }

    /** Registra la nueva credencial como definitiva usando reset-password de la API administrativa. */
    private Mono<Void> cambiarCredencial(String token, String userId, String password) {
        return keycloakAdmin.put()
                .uri("/admin/realms/{realm}/users/{id}/reset-password", realm, userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("type", "password", "value", password, "temporary", false))
                .retrieve()
                .toBodilessEntity()
                .then()
                .onErrorMap(WebClientResponseException.BadRequest.class,
                        error -> new ResponseStatusException(BAD_REQUEST,
                                "La nueva contrasenia no cumple la politica de Keycloak"));
    }

    /** Persiste cambios generales de la representación del usuario en Keycloak. */
    private Mono<Void> actualizarUsuario(String token, String userId, Map<String, Object> usuario) {
        return keycloakAdmin.put()
                .uri("/admin/realms/{realm}/users/{id}", realm, userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(usuario)
                .retrieve()
                .toBodilessEntity()
                .then();
    }

    /** Devuelve una copia de la identidad agregando o retirando UPDATE_PASSWORD. */
    private Map<String, Object> conAccionCambio(Map<String, Object> usuario, boolean incluir) {
        Map<String, Object> copia = new LinkedHashMap<>(usuario);
        List<String> acciones = new ArrayList<>(acciones(usuario));
        acciones.removeIf(UPDATE_PASSWORD::equals);
        if (incluir)
            acciones.add(UPDATE_PASSWORD);
        copia.put("requiredActions", acciones);
        return copia;
    }

    /** Normaliza requiredActions para trabajar siempre con una lista de textos. */
    private List<String> acciones(Map<String, Object> usuario) {
        Object value = usuario.get("requiredActions");
        if (!(value instanceof List<?> lista))
            return new ArrayList<>();
        return lista.stream().map(String::valueOf).toList();
    }

    /** Obtiene un token técnico de admin-cli para ejecutar operaciones administrativas. */
    private Mono<String> tokenAdmin() {
        var form = new LinkedMultiValueMap<String, String>();
        form.add("client_id", "admin-cli");
        form.add("grant_type", "password");
        form.add("username", adminUsername);
        form.add("password", adminPassword);
        return keycloakAdmin.post()
                .uri("/realms/master/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(form)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .map(response -> String.valueOf(response.get("access_token")));
    }

    /** Impide continuar cuando la confirmación no coincide con la nueva contraseña. */
    private void validarConfirmacion(String nueva, String confirmacion) {
        if (!nueva.equals(confirmacion)) {
            throw new ResponseStatusException(BAD_REQUEST, "La confirmacion no coincide");
        }
    }

    /** Exige que la nueva contraseña sea diferente de la anterior o temporal. */
    private void validarDiferente(String actual, String nueva) {
        if (actual.equals(nueva)) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "La contrasenia nueva debe ser diferente a la actual");
        }
    }

    /** Extrae de forma segura un campo textual de una representación genérica de Keycloak. */
    private String texto(Map<String, Object> datos, String campo) {
        Object value = datos.get(campo);
        return value == null ? "" : String.valueOf(value);
    }
}
