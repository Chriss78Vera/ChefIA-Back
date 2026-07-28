package com.util.chefia.auth.service;

import com.util.chefia.auth.dto.LoginRequest;
import com.util.chefia.auth.dto.TokenResponse;
import reactor.core.publisher.Mono;

public interface AutenticacionService {
    Mono<TokenResponse> login(LoginRequest request);
}

