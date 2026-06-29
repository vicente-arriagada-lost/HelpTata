package com.Tutoriales.ms;

// =============================================================
// TESTS UNITARIOS — TutorialServiceTest.java
// =============================================================
// Pruebas de la lógica de negocio de TutorialService usando Mockito.
// No se necesita base de datos: TutorialRepository es un mock.
//
// Patrón AAA usado en cada test:
//   Arrange → configurar los mocks con when(...)
//   Act     → llamar al método del servicio
//   Assert  → verificar resultado con assertEquals / assertThrows
//
// Ejecutar con: ./mvnw test
// =============================================================

import com.Tutoriales.ms.clients.LogClient;
import com.Tutoriales.ms.models.dto.TutorialDTO;
import com.Tutoriales.ms.models.entities.Tutorial;
import com.Tutoriales.ms.models.request.AgregarTutorial;
import com.Tutoriales.ms.models.request.ActualizarTutorial;
import com.Tutoriales.ms.repositories.TutorialRepository;
import com.Tutoriales.ms.services.TutorialService;
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
@DisplayName("TutorialService — pruebas unitarias")
class TutorialServiceTest {

    @Mock
    private TutorialRepository tutorialRepository;

    @Mock
    private LogClient logClient;

    //* @InjectMocks crea una instancia real del servicio e inyecta los mocks
    @InjectMocks
    private TutorialService tutorialService;

    //* Tutorial base reutilizado en los tests para evitar repetición
    private Tutorial tutorialBase;

    @BeforeEach
    void setUp() {
        //* Prepara un tutorial completo con todos los campos obligatorios
        tutorialBase = new Tutorial();
        tutorialBase.setId_tutor(1);
        tutorialBase.setNombre_tuto("Introducción a Java");
        tutorialBase.setCat_tuto("Programación");
        tutorialBase.setNivel_tuto("BASICO");
        tutorialBase.setTutorial("https://youtube.com/abc");
        tutorialBase.setTiempo_tutorial(45);
        tutorialBase.setDescripcion_tuto("Aprende los fundamentos de Java.");
    }

    // ── obtenerTodosLosTutoriales ──────────────────────────────────────────────

    @Test
    @DisplayName("obtenerTodosLosTutoriales: retorna lista con todos los tutoriales")
    void obtenerTodosLosTutoriales_retornaLista() {
        //* Arrange
        when(tutorialRepository.findAll()).thenReturn(List.of(tutorialBase));

        //* Act
        List<TutorialDTO> resultado = tutorialService.obtenerTodosLosTutoriales();

        //* Assert
        assertEquals(1, resultado.size());
        assertEquals("Introducción a Java", resultado.get(0).nombre_tuto());
        verify(tutorialRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("obtenerTodosLosTutoriales: retorna lista vacía si no hay datos")
    void obtenerTodosLosTutoriales_listaVacia() {
        //* Arrange: simula una BD vacía
        when(tutorialRepository.findAll()).thenReturn(List.of());

        assertTrue(tutorialService.obtenerTodosLosTutoriales().isEmpty());
    }

    // ── obtenerTutorialPorId ───────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerTutorialPorId: retorna DTO cuando el ID existe")
    void obtenerTutorialPorId_existente() {
        //* Arrange
        when(tutorialRepository.findById(1)).thenReturn(Optional.of(tutorialBase));

        //* Act
        TutorialDTO resultado = tutorialService.obtenerTutorialPorId(1);

        //* Assert: verifica campos clave del DTO
        assertNotNull(resultado);
        assertEquals(1, resultado.id_tutor());
        assertEquals("BASICO", resultado.nivel_tuto());
    }

    @Test
    @DisplayName("obtenerTutorialPorId: lanza 404 si el ID no existe")
    void obtenerTutorialPorId_noExiste_lanza404() {
        //* Arrange: findById retorna vacío → el servicio debe lanzar 404
        when(tutorialRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> tutorialService.obtenerTutorialPorId(99));
    }

    // ── obtenerTutorialesPorCategoria ──────────────────────────────────────────

    @Test
    @DisplayName("obtenerTutorialesPorCategoria: filtra correctamente por categoría")
    void obtenerTutorialesPorCategoria_filtrado() {
        //* Arrange: solo devuelve tutoriales de "Programación"
        when(tutorialRepository.findByCategoria("Programación"))
                .thenReturn(List.of(tutorialBase));

        //* Act
        List<TutorialDTO> resultado =
                tutorialService.obtenerTutorialesPorCategoria("Programación");

        //* Assert: el DTO retornado tiene la categoría correcta
        assertEquals(1, resultado.size());
        assertEquals("Programación", resultado.get(0).cat_tuto());
    }

    // ── obtenerTutorialesPorNivel ──────────────────────────────────────────────

    @Test
    @DisplayName("obtenerTutorialesPorNivel: filtra correctamente por nivel")
    void obtenerTutorialesPorNivel_filtrado() {
        //* Arrange
        when(tutorialRepository.findByNivel("BASICO"))
                .thenReturn(List.of(tutorialBase));

        //* Act
        List<TutorialDTO> resultado =
                tutorialService.obtenerTutorialesPorNivel("BASICO");

        //* Assert
        assertEquals(1, resultado.size());
        assertEquals("BASICO", resultado.get(0).nivel_tuto());
    }

    // ── agregarTutorial ────────────────────────────────────────────────────────

    @Test
    @DisplayName("agregarTutorial: guarda y retorna el DTO del nuevo tutorial")
    void agregarTutorial_guardaCorrectamente() {
        //* Arrange: request con los datos del nuevo tutorial
        AgregarTutorial req = new AgregarTutorial();
        req.setNombre_tuto("Nuevo Tutorial");
        req.setCat_tuto("Diseño");
        req.setNivel_tuto("INTERMEDIO");
        req.setTutorial("https://youtube.com/nuevo");
        req.setTiempo_tutorial(60);
        req.setDescripcion_tuto("Descripción del nuevo tutorial.");

        //* El mock devuelve tutorialBase como si fuera el registro guardado
        when(tutorialRepository.save(any(Tutorial.class))).thenReturn(tutorialBase);

        //* Act
        TutorialDTO resultado = tutorialService.agregarTutorial(req);

        //* Assert: el DTO no es null y se llamó a save exactamente una vez
        assertNotNull(resultado);
        verify(tutorialRepository, times(1)).save(any(Tutorial.class));
    }

    // ── eliminarTutorial ───────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminarTutorial: elimina correctamente cuando existe")
    void eliminarTutorial_existente() {
        //* Arrange
        when(tutorialRepository.existsById(1)).thenReturn(true);
        doNothing().when(tutorialRepository).deleteById(1);

        //* Act
        String resultado = tutorialService.eliminarTutorial(1);

        //* Assert
        assertEquals("Tutorial eliminado correctamente.", resultado);
        verify(tutorialRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("eliminarTutorial: lanza 404 si el tutorial no existe")
    void eliminarTutorial_noExiste_lanza404() {
        //* Arrange
        when(tutorialRepository.existsById(99)).thenReturn(false);

        //! Si no existe, nunca debe llamarse deleteById
        assertThrows(ResponseStatusException.class,
                () -> tutorialService.eliminarTutorial(99));
        verify(tutorialRepository, never()).deleteById(anyInt());
    }

    // ── actualizarTutorial ─────────────────────────────────────────────────────

    @Test
    @DisplayName("actualizarTutorial: actualiza y retorna el DTO modificado")
    void actualizarTutorial_actualiza() {
        //* Arrange: request con los datos actualizados
        ActualizarTutorial req = new ActualizarTutorial();
        req.setNombre_tuto("Tutorial Actualizado");
        req.setCat_tuto("Programación");
        req.setNivel_tuto("AVANZADO");
        req.setTutorial("https://youtube.com/updated");
        req.setTiempo_tutorial(90);
        req.setDescripcion_tuto("Descripción actualizada.");

        //* findById primero carga la entidad, luego save la persiste
        when(tutorialRepository.findById(1)).thenReturn(Optional.of(tutorialBase));
        when(tutorialRepository.save(any(Tutorial.class))).thenReturn(tutorialBase);

        //* Act
        TutorialDTO resultado = tutorialService.actualizarTutorial(1, req);

        //* Assert
        assertNotNull(resultado);
        verify(tutorialRepository, times(1)).save(any(Tutorial.class));
    }
}
