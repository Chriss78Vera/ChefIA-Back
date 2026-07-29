package com.util.chefia.favoritos.repository;
import com.util.chefia.favoritos.model.Favorito;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface FavoritoRepository extends JpaRepository<Favorito,Long>{
 List<Favorito> findByUsuarioSubOrderByCreadoEnDesc(String sub);
 Optional<Favorito> findByUsuarioSubAndRecetaId(String sub,Long recetaId);
}


