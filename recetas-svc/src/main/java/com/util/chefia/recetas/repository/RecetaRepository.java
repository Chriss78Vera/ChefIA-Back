package com.util.chefia.recetas.repository;

import com.util.chefia.recetas.model.*;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecetaRepository extends JpaRepository<Receta, Long> {
    List<Receta> findByPublicaTrue();
}
