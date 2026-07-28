package com.util.chefia.recetas.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import com.util.chefia.recetas.exception.NotFoundException;
import com.util.chefia.recetas.mapper.RecetaMapper;
import com.util.chefia.recetas.repository.RecetaRepository;
import com.util.chefia.recetas.service.impl.RecetaServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class RecetaServiceTest {
    @Test
    void rechazaUnaRecetaInexistente() {
        RecetaRepository repository = mock(RecetaRepository.class);
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new RecetaServiceImpl(repository, new RecetaMapper()).obtener(99L))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("99");
    }
}

