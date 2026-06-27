package com.Evaluaciones.ms;

// =============================================================
// TESTS UNITARIOS — EvaluacionServiceTest.java
// =============================================================
// Pruebas de la lógica de negocio de EvaluacionService usando Mockito.
// No se necesita base de datos: EvaluacionRepository es un mock.
//
// Patrón AAA usado en cada test:
//   Arrange → configurar los mocks con when(...)
//   Act     → llamar al método del servicio
//   Assert  → verificar resultado con assertEquals / assertThrows
//
// Ejecutar con: ./mvnw test
// =============================================================

import com.Evaluaciones.ms.models.dto.EvaluacionDTO;
import com.Evaluaciones.ms.models.entities.Evaluacion;
import com.Evaluaciones.ms.models.request.AgregarEvaluacion;
import com.Evaluaciones.ms.models.request.ActualizarEvaluacion;
import com.Evaluaciones.ms.repositories.EvaluacionRepository;
import com.Evaluaciones.ms.services.EvaluacionService;
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

//* @ExtendWith habilita la integración de Mockito con JUnit 5
@ExtendWith(MockitoExtension.class)
@DisplayName("EvaluacionService — pruebas unitarias")
class EvaluacionServiceTest {

    @Mock
    private EvaluacionRepository evaluacionRepository;

    //* @InjectMocks crea una instancia real del servicio e inyecta el mock
    @InjectMocks
    private EvaluacionService evaluacionService;

    //* Evaluación base reutilizada en los tests para evitar repetición
    private Evaluacion evaluacionBase;

    @BeforeEach
    void setUp() {
        //* Prepara un quiz de nivel básico asociado al tutorial 1
        evaluacionBase = new Evaluacion();
        evaluacionBase.setId_eva(1);
        evaluacionBase.setNombre_eva("Quiz Java Básico");
        evaluacionBase.setTipo_eva("QUIZ");
        evaluacionBase.setNivel_eva("BASICO");
        evaluacionBase.setBanco_preg(10);
        //* id_tutor es solo una referencia — no se valida contra ms-Tutoriales
        evaluacionBase.setId_tutor(1);
    }

    // ── obtenerTodasLasEvaluaciones ────────────────────────────────────────────

    @Test
    @DisplayName("obtenerTodasLasEvaluaciones: retorna lista con todas las evaluaciones")
    void obtenerTodasLasEvaluaciones_retornaLista() {
        //* Arrange
        when(evaluacionRepository.findAll()).thenReturn(List.of(evaluacionBase));

        //* Act
        List<EvaluacionDTO> resultado = evaluacionService.obtenerTodasLasEvaluaciones();

        //* Assert
        assertEquals(1, resultado.size());
        assertEquals("Quiz Java Básico", resultado.get(0).nombre_eva());
        verify(evaluacionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("obtenerTodasLasEvaluaciones: retorna lista vacía si no hay datos")
    void obtenerTodasLasEvaluaciones_listaVacia() {
        //* Arrange: BD sin registros
        when(evaluacionRepository.findAll()).thenReturn(List.of());

        assertTrue(evaluacionService.obtenerTodasLasEvaluaciones().isEmpty());
    }

    // ── obtenerEvaluacionPorId ─────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerEvaluacionPorId: retorna DTO cuando el ID existe")
    void obtenerEvaluacionPorId_existente() {
        //* Arrange
        when(evaluacionRepository.findById(1)).thenReturn(Optional.of(evaluacionBase));

        //* Act
        EvaluacionDTO resultado = evaluacionService.obtenerEvaluacionPorId(1);

        //* Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.id_eva());
        assertEquals("QUIZ", resultado.tipo_eva());
    }

    @Test
    @DisplayName("obtenerEvaluacionPorId: lanza 404 si el ID no existe")
    void obtenerEvaluacionPorId_noExiste_lanza404() {
        //* Arrange
        when(evaluacionRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> evaluacionService.obtenerEvaluacionPorId(99));
    }

    // ── obtenerEvaluacionesPorTutorial ─────────────────────────────────────────

    @Test
    @DisplayName("obtenerEvaluacionesPorTutorial: filtra por id_tutor correctamente")
    void obtenerEvaluacionesPorTutorial_filtrado() {
        //* Arrange
        when(evaluacionRepository.findByIdTutor(1)).thenReturn(List.of(evaluacionBase));

        //* Act
        List<EvaluacionDTO> resultado =
                evaluacionService.obtenerEvaluacionesPorTutorial(1);

        //* Assert
        assertEquals(1, resultado.size());
        assertEquals(1, resultado.get(0).id_tutor());
    }

    // ── obtenerEvaluacionesPorNivel ────────────────────────────────────────────

    @Test
    @DisplayName("obtenerEvaluacionesPorNivel: filtra por nivel correctamente")
    void obtenerEvaluacionesPorNivel_filtrado() {
        //* Arrange
        when(evaluacionRepository.findByNivel_eva("BASICO"))
                .thenReturn(List.of(evaluacionBase));

        //* Act
        List<EvaluacionDTO> resultado =
                evaluacionService.obtenerEvaluacionesPorNivel("BASICO");

        //* Assert
        assertEquals(1, resultado.size());
        assertEquals("BASICO", resultado.get(0).nivel_eva());
    }

    // ── obtenerEvaluacionesPorTipo ─────────────────────────────────────────────

    @Test
    @DisplayName("obtenerEvaluacionesPorTipo: filtra por tipo QUIZ correctamente")
    void obtenerEvaluacionesPorTipo_filtrado() {
        //* Arrange
        when(evaluacionRepository.findByTipo_eva("QUIZ"))
                .thenReturn(List.of(evaluacionBase));

        //* Act
        List<EvaluacionDTO> resultado =
                evaluacionService.obtenerEvaluacionesPorTipo("QUIZ");

        //* Assert
        assertEquals(1, resultado.size());
        assertEquals("QUIZ", resultado.get(0).tipo_eva());
    }

    // ── agregarEvaluacion ──────────────────────────────────────────────────────

    @Test
    @DisplayName("agregarEvaluacion: guarda y retorna el DTO de la nueva evaluación")
    void agregarEvaluacion_guardaCorrectamente() {
        //* Arrange
        AgregarEvaluacion req = new AgregarEvaluacion();
        req.setNombre_eva("Examen Final");
        req.setTipo_eva("EXAMEN");
        req.setNivel_eva("AVANZADO");
        req.setBanco_preg(20);
        req.setId_tutor(2);

        //* El mock devuelve evaluacionBase como si fuera el registro guardado
        when(evaluacionRepository.save(any(Evaluacion.class))).thenReturn(evaluacionBase);

        //* Act
        EvaluacionDTO resultado = evaluacionService.agregarEvaluacion(req);

        //* Assert
        assertNotNull(resultado);
        verify(evaluacionRepository, times(1)).save(any(Evaluacion.class));
    }

    // ── actualizarEvaluacion ───────────────────────────────────────────────────

    @Test
    @DisplayName("actualizarEvaluacion: actualiza y retorna el DTO modificado")
    void actualizarEvaluacion_actualiza() {
        //* Arrange
        ActualizarEvaluacion req = new ActualizarEvaluacion();
        req.setNombre_eva("Quiz Actualizado");
        req.setTipo_eva("QUIZ");
        req.setNivel_eva("INTERMEDIO");
        req.setBanco_preg(15);
        req.setId_tutor(1);

        //* findById carga la entidad existente, save la persiste con los nuevos datos
        when(evaluacionRepository.findById(1)).thenReturn(Optional.of(evaluacionBase));
        when(evaluacionRepository.save(any(Evaluacion.class))).thenReturn(evaluacionBase);

        //* Act
        EvaluacionDTO resultado = evaluacionService.actualizarEvaluacion(1, req);

        //* Assert
        assertNotNull(resultado);
        verify(evaluacionRepository, times(1)).save(any(Evaluacion.class));
    }

    @Test
    @DisplayName("actualizarEvaluacion: lanza 404 si la evaluación no existe")
    void actualizarEvaluacion_noExiste_lanza404() {
        //* Arrange
        ActualizarEvaluacion req = new ActualizarEvaluacion();
        req.setNombre_eva("No existe");
        req.setTipo_eva("QUIZ");
        req.setNivel_eva("BASICO");
        req.setBanco_preg(5);
        req.setId_tutor(1);

        when(evaluacionRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> evaluacionService.actualizarEvaluacion(99, req));
    }

    // ── eliminarEvaluacion ─────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminarEvaluacion: elimina correctamente cuando existe")
    void eliminarEvaluacion_existente() {
        //* Arrange
        when(evaluacionRepository.existsById(1)).thenReturn(true);
        doNothing().when(evaluacionRepository).deleteById(1);

        //* Act
        String resultado = evaluacionService.eliminarEvaluacion(1);

        //* Assert
        assertEquals("Evaluación eliminada correctamente.", resultado);
        verify(evaluacionRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("eliminarEvaluacion: lanza 404 si la evaluación no existe")
    void eliminarEvaluacion_noExiste_lanza404() {
        //* Arrange
        when(evaluacionRepository.existsById(99)).thenReturn(false);

        //! Si no existe, nunca debe llamarse deleteById
        assertThrows(ResponseStatusException.class,
                () -> evaluacionService.eliminarEvaluacion(99));
    }
}
