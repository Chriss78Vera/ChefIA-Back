package com.util.chefia.auth.service;

import com.util.chefia.auth.dto.CambioContraseniaRequest;
import com.util.chefia.auth.dto.CambioContraseniaTemporalRequest;
import com.util.chefia.auth.dto.MensajeResponse;
import reactor.core.publisher.Mono;

public interface ContraseniaService {
    Mono<MensajeResponse> cambiar(String userId, String username, CambioContraseniaRequest request);

    Mono<MensajeResponse> cambiarTemporal(CambioContraseniaTemporalRequest request);
}
