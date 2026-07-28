package com.util.chefia.auth.service;

import com.util.chefia.auth.dto.CrearUsuarioRequest;
import com.util.chefia.auth.dto.UsuarioCreadoResponse;
import com.util.chefia.auth.dto.UsuarioAdminResponse;
import java.util.List;
import reactor.core.publisher.Mono;

public interface KeycloakAdminService {
    Mono<UsuarioCreadoResponse> crear(CrearUsuarioRequest request);
    Mono<UsuarioCreadoResponse> crearTemporal(CrearUsuarioRequest request);
    Mono<List<UsuarioAdminResponse>> listarUsuariosNormales();
    Mono<UsuarioAdminResponse> cambiarEstado(String id, boolean activo);
}

