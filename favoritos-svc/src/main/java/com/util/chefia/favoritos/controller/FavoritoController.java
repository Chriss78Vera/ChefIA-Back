package com.util.chefia.favoritos.controller;
import com.util.chefia.favoritos.dto.FavoritoResponse;
import com.util.chefia.favoritos.mapper.FavoritoMapper;
import com.util.chefia.favoritos.service.FavoritoService;
import java.util.List;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/favoritos") @PreAuthorize("hasAnyRole('USUARIO','ADMIN')")
public class FavoritoController {
 private final FavoritoService service; private final FavoritoMapper mapper;
 public FavoritoController(FavoritoService service,FavoritoMapper mapper){this.service=service;this.mapper=mapper;}
 @GetMapping public List<FavoritoResponse> listar(@AuthenticationPrincipal Jwt jwt){return service.listar(jwt.getSubject()).stream().map(mapper::aResponse).toList();}
 @PostMapping("/{recetaId}") public ResponseEntity<FavoritoResponse> agregar(@AuthenticationPrincipal Jwt jwt,@PathVariable Long recetaId){return ResponseEntity.status(HttpStatus.CREATED).body(mapper.aResponse(service.agregar(jwt.getSubject(),recetaId,jwt.getTokenValue())));}
 @DeleteMapping("/{recetaId}") @ResponseStatus(HttpStatus.NO_CONTENT) public void eliminar(@AuthenticationPrincipal Jwt jwt,@PathVariable Long recetaId){service.eliminar(jwt.getSubject(),recetaId);}
}

