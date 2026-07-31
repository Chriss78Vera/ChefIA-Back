package com.util.chefia.favoritos.service.impl;

import com.util.chefia.favoritos.client.RecetasClient;
import com.util.chefia.favoritos.model.Favorito;
import com.util.chefia.favoritos.repository.FavoritoRepository;
import com.util.chefia.favoritos.service.FavoritoService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoritoServiceImpl implements FavoritoService {
    private final FavoritoRepository repository;
    private final RecetasClient recetas;

    @Override
    @Transactional(readOnly = true)
    public List<Favorito> listar(String sub) {
        return repository.findByUsuarioSubOrderByCreadoEnDesc(sub);
    }

    @Override
    @Transactional
    public Favorito agregar(String sub, Long id, String token) {
        if (repository.findByUsuarioSubAndRecetaId(sub, id).isPresent())
            throw new ResponseStatusException(CONFLICT, "La receta ya esta en favoritos");
        var receta = recetas.obtener(id, token);
        return repository.save(new Favorito(sub, id, receta.nombre()));
    }

    @Override
    @Transactional
    public void eliminar(String sub, Long id) {
        var favorito = repository.findByUsuarioSubAndRecetaId(sub, id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Favorito no encontrado"));
        repository.delete(favorito);
    }
}
