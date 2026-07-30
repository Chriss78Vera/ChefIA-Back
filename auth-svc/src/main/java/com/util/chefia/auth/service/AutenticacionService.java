package com.util.chefia.auth.service;

import com.util.chefia.auth.dto.LoginRequest;
import com.util.chefia.auth.dto.TokenResponse;
import reactor.core.publisher.Mono;

/** Contrato del flujo de autenticación contra Keycloak. */
public interface AutenticacionService {
    /** Valida las credenciales y devuelve los tokens emitidos por el realm. */
    Mono<TokenResponse> login(LoginRequest request);
}
