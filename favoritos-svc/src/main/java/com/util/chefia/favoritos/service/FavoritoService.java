package com.util.chefia.favoritos.service;

import com.util.chefia.favoritos.model.Favorito;
import java.util.List;

public interface FavoritoService {
    List<Favorito> listar(String sub);

    Favorito agregar(String sub, Long id, String token);

    void eliminar(String sub, Long id);
}
