package com.util.chefia.recomendaciones.service;

import com.util.chefia.recomendaciones.client.RecetasClient;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CatalogoResilienteService {
    CompletableFuture<List<RecetasClient.RecetaCandidata>> candidatas(String tipo, String authorization);
}
