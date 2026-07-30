package com.util.chefia.auth.service;

import com.util.chefia.auth.dto.CrearUsuarioRequest;
import com.util.chefia.auth.dto.UsuarioCreadoResponse;
import com.util.chefia.auth.dto.UsuarioAdminResponse;
import java.util.List;
import reactor.core.publisher.Mono;

/** Contrato para administrar identidades mediante la API administrativa de Keycloak. */
public interface KeycloakAdminService {
    /** Crea una cuenta pública con credencial definitiva. */
    Mono<UsuarioCreadoResponse> crear(CrearUsuarioRequest request);

    /** Crea una cuenta desde administración con credencial temporal. */
    Mono<UsuarioCreadoResponse> crearTemporal(CrearUsuarioRequest request);

    /** Lista cuentas normales y excluye identidades con rol ADMIN. */
    Mono<List<UsuarioAdminResponse>> listarUsuariosNormales();

    /** Actualiza enabled después de impedir cambios sobre administradores. */
    Mono<UsuarioAdminResponse> cambiarEstado(String id, boolean activo);
}
