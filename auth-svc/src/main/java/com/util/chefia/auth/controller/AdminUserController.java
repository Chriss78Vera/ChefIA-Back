package com.util.chefia.auth.controller;

import com.util.chefia.auth.dto.CrearUsuarioRequest;
import com.util.chefia.auth.dto.UsuarioCreadoResponse;
import com.util.chefia.auth.dto.UsuarioAdminResponse;
import com.util.chefia.auth.dto.EstadoUsuarioRequest;
import java.util.List;
import com.util.chefia.auth.service.KeycloakAdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/usuarios")
@RequiredArgsConstructor
/** Expone operaciones administrativas sobre cuentas normales de Keycloak. */
public class AdminUserController {
    private final KeycloakAdminService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    /** Crea un usuario con contraseña temporal y cambio obligatorio en su primer acceso. */
    public Mono<UsuarioCreadoResponse> crear(@Valid @RequestBody CrearUsuarioRequest request) {
        return service.crearTemporal(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    /** Lista únicamente cuentas que no poseen el rol ADMIN. */
    public Mono<List<UsuarioAdminResponse>> listar() {
        return service.listarUsuariosNormales();
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    /** Activa o desactiva una cuenta sin eliminarla de Keycloak. */
    public Mono<UsuarioAdminResponse> cambiarEstado(@PathVariable String id,
            @Valid @RequestBody EstadoUsuarioRequest request) {
        return service.cambiarEstado(id, request.activo());
    }
}
