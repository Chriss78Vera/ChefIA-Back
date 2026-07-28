package com.util.chefia.recetas.service;
import com.util.chefia.recetas.dto.*;
import com.util.chefia.recetas.model.*;
import java.util.List;
public interface RecetaService {
 List<Receta> listar(TipoAlimentacion tipo,Integer tiempo);
 Receta obtener(Long id);
 Receta crear(RecetaRequest request);
 Receta crearUsuario(RecetaUsuarioRequest request,String usuarioSub);
 List<Receta> listarPublicas(Animo animo,TipoAlimentacion tipo,TipoReceta tipoReceta);
 Receta obtenerVisible(Long id,String usuarioSub);
 Receta editar(Long id,RecetaRequest request);
 void eliminar(Long id);
}

