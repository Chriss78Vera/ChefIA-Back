package com.util.chefia.recetas.repository;
import com.util.chefia.recetas.model.*;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface RecetaRepository extends JpaRepository<Receta,Long> {
 List<Receta> findByTipoAlimentacionAndTiempoMinutosLessThanEqual(TipoAlimentacion tipo,Integer tiempo);
 List<Receta> findByPublicaTrue();
 List<Receta> findByPublicaTrueAndAnimoAndTipoAlimentacion(Animo animo,TipoAlimentacion tipo);
}


