package com.PreguntasRespuestas.ms;

import com.PreguntasRespuestas.ms.models.dto.CuestionarioDTO;
import com.PreguntasRespuestas.ms.models.entities.Cuestionario;
import com.PreguntasRespuestas.ms.models.request.ActualizarCuestionario;
import com.PreguntasRespuestas.ms.models.request.AgregarCuestionario;
import com.PreguntasRespuestas.ms.repositories.CuestionarioRepository;
import com.PreguntasRespuestas.ms.services.CuestionarioService;
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
@DisplayName("CuestionarioService — pruebas unitarias")
class CuestionarioServiceTest {

    @Mock private CuestionarioRepository cuestionarioRepository;
    @InjectMocks private CuestionarioService cuestionarioService;

    private Cuestionario cuestionarioBase;

    @BeforeEach
    void setUp() {
        cuestionarioBase = new Cuestionario();
        cuestionarioBase.setId_cuestionario(1);
        cuestionarioBase.setTitulo_cuestionario("Quiz WhatsApp");
        cuestionarioBase.setDescripcion_cuestionario("Preguntas sobre mensajería");
        cuestionarioBase.setId_tutor(1);
    }

    // ── obtenerTodosLosCuestionarios ──────────────────────────────────────────

    @Test
    @DisplayName("obtenerTodos: retorna lista de DTOs cuando hay cuestionarios")
    void obtenerTodos_retornaLista() {
        when(cuestionarioRepository.findAll()).thenReturn(List.of(cuestionarioBase));

        List<CuestionarioDTO> resultado = cuestionarioService.obtenerTodosLosCuestionarios();

        assertEquals(1, resultado.size());
        assertEquals("Quiz WhatsApp", resultado.get(0).titulo_cuestionario());
        verify(cuestionarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("obtenerTodos: retorna lista vacía cuando no hay cuestionarios")
    void obtenerTodos_listaVacia() {
        when(cuestionarioRepository.findAll()).thenReturn(List.of());

        assertTrue(cuestionarioService.obtenerTodosLosCuestionarios().isEmpty());
    }

    // ── obtenerCuestionarioPorId ──────────────────────────────────────────────

    @Test
    @DisplayName("obtenerPorId: retorna DTO cuando el ID existe")
    void obtenerPorId_existente() {
        when(cuestionarioRepository.findById(1)).thenReturn(Optional.of(cuestionarioBase));

        CuestionarioDTO resultado = cuestionarioService.obtenerCuestionarioPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.id_cuestionario());
        assertEquals(1, resultado.id_tutor());
    }

    @Test
    @DisplayName("obtenerPorId: lanza 404 cuando el ID no existe")
    void obtenerPorId_noExiste_lanza404() {
        when(cuestionarioRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> cuestionarioService.obtenerCuestionarioPorId(99));
    }

    // ── obtenerCuestionariosPorTutorial ──────────────────────────────────────

    @Test
    @DisplayName("obtenerPorTutorial: retorna cuestionarios asociados al tutorial")
    void obtenerPorTutorial_retornaLista() {
        when(cuestionarioRepository.findByIdTutor(1)).thenReturn(List.of(cuestionarioBase));

        List<CuestionarioDTO> resultado = cuestionarioService.obtenerCuestionariosPorTutorial(1);

        assertEquals(1, resultado.size());
        assertEquals(1, resultado.get(0).id_tutor());
    }

    @Test
    @DisplayName("obtenerPorTutorial: retorna lista vacía si el tutorial no tiene cuestionarios")
    void obtenerPorTutorial_sinCuestionarios() {
        when(cuestionarioRepository.findByIdTutor(99)).thenReturn(List.of());

        assertTrue(cuestionarioService.obtenerCuestionariosPorTutorial(99).isEmpty());
    }

    // ── agregarCuestionario ───────────────────────────────────────────────────

    @Test
    @DisplayName("agregarCuestionario: guarda y retorna el DTO con los datos correctos")
    void agregar_guardaYRetornaDTO() {
        AgregarCuestionario req = new AgregarCuestionario();
        req.setTitulo_cuestionario("Nuevo Quiz");
        req.setDescripcion_cuestionario("Descripción");
        req.setId_tutor(2);

        when(cuestionarioRepository.save(any(Cuestionario.class))).thenAnswer(inv -> {
            Cuestionario c = inv.getArgument(0);
            c.setId_cuestionario(5);
            return c;
        });

        CuestionarioDTO resultado = cuestionarioService.agregarCuestionario(req);

        assertEquals("Nuevo Quiz", resultado.titulo_cuestionario());
        assertEquals(2, resultado.id_tutor());
        verify(cuestionarioRepository, times(1)).save(any(Cuestionario.class));
    }

    // ── actualizarCuestionario ────────────────────────────────────────────────

    @Test
    @DisplayName("actualizarCuestionario: actualiza campos y retorna DTO actualizado")
    void actualizar_exitoso() {
        ActualizarCuestionario req = new ActualizarCuestionario();
        req.setTitulo_cuestionario("Quiz Actualizado");
        req.setDescripcion_cuestionario("Nueva descripción");
        req.setId_tutor(1);

        when(cuestionarioRepository.findById(1)).thenReturn(Optional.of(cuestionarioBase));
        when(cuestionarioRepository.save(any(Cuestionario.class))).thenAnswer(inv -> inv.getArgument(0));

        CuestionarioDTO resultado = cuestionarioService.actualizarCuestionario(1, req);

        assertEquals("Quiz Actualizado", resultado.titulo_cuestionario());
        assertEquals("Nueva descripción", resultado.descripcion_cuestionario());
    }

    @Test
    @DisplayName("actualizarCuestionario: lanza 404 si el cuestionario no existe")
    void actualizar_noExiste_lanza404() {
        when(cuestionarioRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> cuestionarioService.actualizarCuestionario(99, new ActualizarCuestionario()));
    }

    // ── eliminarCuestionario ──────────────────────────────────────────────────

    @Test
    @DisplayName("eliminarCuestionario: elimina correctamente y retorna mensaje")
    void eliminar_exitoso() {
        when(cuestionarioRepository.existsById(1)).thenReturn(true);
        doNothing().when(cuestionarioRepository).deleteById(1);

        String resultado = cuestionarioService.eliminarCuestionario(1);

        assertTrue(resultado.contains("eliminado correctamente"));
        verify(cuestionarioRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("eliminarCuestionario: lanza 404 si no existe y nunca llama deleteById")
    void eliminar_noExiste_lanza404() {
        when(cuestionarioRepository.existsById(99)).thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> cuestionarioService.eliminarCuestionario(99));
        verify(cuestionarioRepository, never()).deleteById(anyInt());
    }
}
