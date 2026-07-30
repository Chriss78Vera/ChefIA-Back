package com.util.chefia.auth.service;

import com.util.chefia.auth.dto.CambioContraseniaRequest;
import com.util.chefia.auth.dto.CambioContraseniaTemporalRequest;
import com.util.chefia.auth.dto.MensajeResponse;
import reactor.core.publisher.Mono;

/** Contrato de cambios de credenciales definitivas y temporales. */
public interface ContraseniaService {
    /** Cambia la credencial de una cuenta autenticada después de validar su clave actual. */
    Mono<MensajeResponse> cambiar(String userId, String username, CambioContraseniaRequest request);

    /** Completa el cambio obligatorio asociado con la acción UPDATE_PASSWORD. */
    Mono<MensajeResponse> cambiarTemporal(CambioContraseniaTemporalRequest request);
}
