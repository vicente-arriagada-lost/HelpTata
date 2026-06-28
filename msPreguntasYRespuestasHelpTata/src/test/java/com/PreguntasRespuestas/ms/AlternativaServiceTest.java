package com.PreguntasRespuestas.ms;

import com.PreguntasRespuestas.ms.models.dto.AlternativaDTO;
import com.PreguntasRespuestas.ms.models.entities.Alternativa;
import com.PreguntasRespuestas.ms.models.request.AgregarAlternativa;
import com.PreguntasRespuestas.ms.repositories.AlternativaRepository;
import com.PreguntasRespuestas.ms.repositories.PreguntaRepository;
import com.PreguntasRespuestas.ms.services.AlternativaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlternativaService — pruebas unitarias")
class AlternativaServiceTest {

    @Mock private AlternativaRepository alternativaRepository;
    @Mock private PreguntaRepository preguntaRepository;
    @InjectMocks private AlternativaService alternativaService;

    private Alternativa alternativaBase;

    @BeforeEach
    void setUp() {
        alternativaBase = new Alternativa();
        alternativaBase.setId_alternativa(1);
        alternativaBase.setTexto_alternativa("Presionar el ícono de enviar");
        alternativaBase.setEs_correcta(true);
        alternativaBase.setId_pregunta(1);
    }

    // ── obtenerAlternativasPorPregunta ────────────────────────────────────────

    @Test
    @DisplayName("obtenerPorPregunta: retorna alternativas cuando la pregunta existe")
    void obtenerPorPregunta_exitoso() {
        when(preguntaRepository.existsById(1)).thenReturn(true);
        when(alternativaRepository.findByIdPregunta(1)).thenReturn(List.of(alternativaBase));

        List<AlternativaDTO> resultado = alternativaService.obtenerAlternativasPorPregunta(1);

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).es_correcta());
    }

    @Test
    @DisplayName("obtenerPorPregunta: lanza 404 cuando la pregunta no existe")
    void obtenerPorPregunta_preguntaNoExiste_lanza404() {
        when(preguntaRepository.existsById(99)).thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> alternativaService.obtenerAlternativasPorPregunta(99));
        verify(alternativaRepository, never()).findByIdPregunta(anyInt());
    }

    // ── agregarAlternativa ────────────────────────────────────────────────────

    @Test
    @DisplayName("agregarAlternativa: guarda correctamente cuando la pregunta existe")
    void agregar_exitoso() {
        AgregarAlternativa req = new AgregarAlternativa();
        req.setTexto_alternativa("Tocar el micrófono");
        req.setEs_correcta(false);
        req.setId_pregunta(1);

        when(preguntaRepository.existsById(1)).thenReturn(true);
        when(alternativaRepository.save(any(Alternativa.class))).thenAnswer(inv -> {
            Alternativa a = inv.getArgument(0);
            a.setId_alternativa(2);
            return a;
        });

        AlternativaDTO resultado = alternativaService.agregarAlternativa(req);

        assertEquals("Tocar el micrófono", resultado.texto_alternativa());
        assertFalse(resultado.es_correcta());
        verify(alternativaRepository, times(1)).save(any(Alternativa.class));
    }

    @Test
    @DisplayName("agregarAlternativa: lanza 404 cuando la pregunta referenciada no existe")
    void agregar_preguntaNoExiste_lanza404() {
        AgregarAlternativa req = new AgregarAlternativa();
        req.setId_pregunta(99);

        when(preguntaRepository.existsById(99)).thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> alternativaService.agregarAlternativa(req));
        verify(alternativaRepository, never()).save(any());
    }

    // ── obtenerAlternativaPorId ───────────────────────────────────────────────

    @Test
    @DisplayName("obtenerPorId: retorna DTO cuando existe")
    void obtenerPorId_existente() {
        when(alternativaRepository.findById(1)).thenReturn(Optional.of(alternativaBase));

        AlternativaDTO resultado = alternativaService.obtenerAlternativaPorId(1);

        assertEquals(1, resultado.id_alternativa());
        assertEquals("Presionar el ícono de enviar", resultado.texto_alternativa());
    }

    @Test
    @DisplayName("obtenerPorId: lanza 404 cuando no existe")
    void obtenerPorId_noExiste_lanza404() {
        when(alternativaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> alternativaService.obtenerAlternativaPorId(99));
    }

    // ── eliminarAlternativa ───────────────────────────────────────────────────

    @Test
    @DisplayName("eliminar: elimina correctamente cuando existe")
    void eliminar_exitoso() {
        when(alternativaRepository.existsById(1)).thenReturn(true);
        doNothing().when(alternativaRepository).deleteById(1);

        String resultado = alternativaService.eliminarAlternativa(1);

        assertTrue(resultado.contains("eliminada correctamente"));
        verify(alternativaRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("eliminar: lanza 404 y nunca llama deleteById cuando no existe")
    void eliminar_noExiste_lanza404() {
        when(alternativaRepository.existsById(99)).thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> alternativaService.eliminarAlternativa(99));
        verify(alternativaRepository, never()).deleteById(anyInt());
    }
}
