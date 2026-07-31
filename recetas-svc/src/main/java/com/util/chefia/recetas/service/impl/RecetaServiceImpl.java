package com.util.chefia.recetas.service.impl;

import com.util.chefia.recetas.dto.*;
import com.util.chefia.recetas.exception.NotFoundException;
import com.util.chefia.recetas.mapper.RecetaMapper;
import com.util.chefia.recetas.model.Receta;
import com.util.chefia.recetas.model.TipoAlimentacion;
import com.util.chefia.recetas.model.Animo;
import com.util.chefia.recetas.model.TipoReceta;
import com.util.chefia.recetas.repository.RecetaRepository;
import com.util.chefia.recetas.service.RecetaService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecetaServiceImpl implements RecetaService {
  private final RecetaRepository repository;
  private final RecetaMapper mapper;

  @Override
  @Transactional(readOnly = true)
  public List<Receta> listar(TipoAlimentacion tipo, Integer tiempo) {
    return repository.findByPublicaTrue().stream().filter(r -> tipo == null || r.getTipoAlimentacion() == tipo)
        .filter(r -> tiempo == null || r.getTiempoMinutos() <= tiempo).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public Receta obtener(Long id) {
    return repository.findById(id).orElseThrow(() -> new NotFoundException("Receta no encontrada: " + id));
  }

  @Override
  @Transactional
  public Receta crear(RecetaRequest request) {
    Receta entity = new Receta();
    mapper.actualizarEntidad(request, entity);
    return repository.save(entity);
  }

  @Override
  @Transactional
  public Receta crearUsuario(RecetaUsuarioRequest r, String sub) {
    Receta entity = new Receta();
    entity.actualizar(r.nombre(), r.descripcion(), r.tiempoMinutos(), r.dificultad(), r.tipoAlimentacion(),
        r.ingredientes(), r.ingredienteAditional());
    entity.configurarUsuario(sub, r.publica(), r.animo(), r.tipoReceta(), r.porciones(), r.pasos(), r.tags(),
        r.ingredienteAditional());
    return repository.save(entity);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Receta> listarPublicas(Animo animo, TipoAlimentacion tipo, TipoReceta tipoReceta) {
    return repository.findByPublicaTrue().stream()
        .filter(r -> animo == null || r.getAnimo() == null || r.getAnimo() == animo)
        .filter(r -> tipo == null || r.getTipoAlimentacion() == tipo)
        .filter(r -> tipoReceta == null || r.getTipoReceta() == tipoReceta).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public Receta obtenerVisible(Long id, String sub) {
    Receta receta = obtener(id);
    if (!receta.isPublica() && !java.util.Objects.equals(receta.getUsuarioSub(), sub))
      throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN,
          "Receta privada");
    return receta;
  }

  @Override
  @Transactional
  public Receta editar(Long id, RecetaRequest request) {
    Receta entity = obtener(id);
    mapper.actualizarEntidad(request, entity);
    return repository.save(entity);
  }

  @Override
  @Transactional
  public void eliminar(Long id) {
    repository.delete(obtener(id));
  }
}
