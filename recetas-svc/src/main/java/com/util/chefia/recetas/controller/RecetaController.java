package com.util.chefia.recetas.controller;

import com.util.chefia.recetas.dto.RecetaRequest;
import com.util.chefia.recetas.dto.RecetaResponse;
import com.util.chefia.recetas.dto.RecetaUsuarioRequest;
import com.util.chefia.recetas.mapper.RecetaMapper;
import com.util.chefia.recetas.model.*;
import com.util.chefia.recetas.service.RecetaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@RequestMapping("/api/recetas")
public class RecetaController {
    private final RecetaService service;
    private final RecetaMapper mapper;

    public RecetaController(RecetaService service, RecetaMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USUARIO','ADMIN')")
    public List<RecetaResponse> listar(@RequestParam(required = false) TipoAlimentacion tipoAlimentacion,
            @RequestParam(required = false) Integer tiempoMaximo) {
        return service.listar(tipoAlimentacion, tiempoMaximo).stream().map(mapper::aResponse).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USUARIO','ADMIN')")
    public RecetaResponse obtener(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return mapper.aResponse(service.obtenerVisible(id, jwt.getSubject()));
    }

    @GetMapping("/publicas")
    @PreAuthorize("hasAnyRole('USUARIO','ADMIN')")
    public List<RecetaResponse> publicas(@RequestParam(required = false) Animo animo,
            @RequestParam(required = false) TipoAlimentacion tipoAlimentacion,
            @RequestParam(required = false) TipoReceta tipoReceta) {
        return service.listarPublicas(animo, tipoAlimentacion, tipoReceta).stream().map(mapper::aResponse).toList();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USUARIO','ADMIN')")
    public ResponseEntity<RecetaResponse> crearUsuario(@Valid @RequestBody RecetaUsuarioRequest r,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.aResponse(service.crearUsuario(r, jwt.getSubject())));
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecetaResponse> crear(@Valid @RequestBody RecetaRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.aResponse(service.crear(r)));
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RecetaResponse editar(@PathVariable Long id, @Valid @RequestBody RecetaRequest r) {
        return mapper.aResponse(service.editar(id, r));
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
