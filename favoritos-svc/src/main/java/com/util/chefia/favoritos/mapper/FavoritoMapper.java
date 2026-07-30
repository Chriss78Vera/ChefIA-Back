package com.util.chefia.favoritos.mapper;

import com.util.chefia.favoritos.dto.FavoritoResponse;
import com.util.chefia.favoritos.model.Favorito;
import org.springframework.stereotype.Component;

@Component
public class FavoritoMapper {
    public FavoritoResponse aResponse(Favorito favorito) {
        return new FavoritoResponse(
                favorito.getId(),
                favorito.getRecetaId(),
                favorito.getRecetaNombre(),
                favorito.getCreadoEn());
    }
}
