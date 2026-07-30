package com.util.chefia.recomendaciones.repository;

import com.util.chefia.recomendaciones.model.RecomendacionCache;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecomendacionCacheRepository extends JpaRepository<RecomendacionCache, Long> {
    Optional<RecomendacionCache> findFirstByUsuarioSubAndAnimoAndTipoRecetaAndPreferenciaOrderByCreadoEnDesc(
            String usuarioSub, String animo, String tipoReceta, String preferencia);

    List<RecomendacionCache> findByUsuarioSubOrderByCreadoEnDesc(String usuarioSub);
}
