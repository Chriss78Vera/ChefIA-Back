package com.util.chefia.auth.controller;

import com.util.chefia.auth.dto.LoginRequest;
import com.util.chefia.auth.dto.TokenResponse;
import com.util.chefia.auth.dto.CrearUsuarioRequest;
import com.util.chefia.auth.dto.UsuarioCreadoResponse;
import com.util.chefia.auth.dto.CambioContraseniaRequest;
import com.util.chefia.auth.dto.CambioContraseniaTemporalRequest;
import com.util.chefia.auth.dto.MensajeResponse;
import com.util.chefia.auth.service.AutenticacionService;
import com.util.chefia.auth.service.ContraseniaService;
import com.util.chefia.auth.service.KeycloakAdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
/** Publica los flujos de acceso, registro y cambio de contraseña. */
public class AuthController {
    private final AutenticacionService service;
    private final KeycloakAdminService usuarios;
    private final ContraseniaService contrasenias;

    public AuthController(AutenticacionService service, KeycloakAdminService usuarios,
            ContraseniaService contrasenias) {
        this.service = service;
        this.usuarios = usuarios;
        this.contrasenias = contrasenias;
    }

    @PostMapping("/login")
    /** Intercambia credenciales válidas por los tokens emitidos por Keycloak. */
    public Mono<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return service.login(request);
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    /** Registra una cuenta normal con contraseña definitiva y rol USUARIO. */
    public Mono<UsuarioCreadoResponse> registrar(@Valid @RequestBody CrearUsuarioRequest request) {
        return usuarios.crear(request);
    }

    @PostMapping("/contrasenia-temporal")
    /** Permite completar el primer ingreso de una cuenta creada por un administrador. */
    public Mono<MensajeResponse> cambiarTemporal(
            @Valid @RequestBody CambioContraseniaTemporalRequest request) {
        return contrasenias.cambiarTemporal(request);
    }

    @PutMapping("/contrasenia")
    /** Cambia la contraseña de la identidad indicada por el JWT y verifica primero la clave actual. */
    public Mono<MensajeResponse> cambiar(@AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CambioContraseniaRequest request) {
        return contrasenias.cambiar(jwt.getSubject(), jwt.getClaimAsString("preferred_username"), request);
    }
}
