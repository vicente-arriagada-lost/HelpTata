package com.Direccion.ms;

// =============================================================
// TESTS UNITARIOS — DireccionServiceTest.java
// =============================================================
// Pruebas de la lógica de negocio de DireccionService usando Mockito.
//
// Caso especial probado:
//   agregarDireccion_comunaInexistente_lanza404 — antes de guardar
//   una dirección, el servicio valida que la comuna referenciada
//   exista en la BD. Si no existe, lanza HTTP 404 y NO persiste
//   la dirección (protege la integridad referencial sin FK cruzada).
//
// Ejecutar con: ./mvnw test
// =============================================================

import com.Direccion.ms.models.dto.DireccionDTO;
import com.Direccion.ms.models.entities.Comuna;
import com.Direccion.ms.models.entities.Direccion;
import com.Direccion.ms.models.request.AgregarDireccion;
import com.Direccion.ms.models.request.ActualizarDireccion;
import com.Direccion.ms.repositories.ComunaRepository;
import com.Direccion.ms.repositories.DireccionRepository;
import com.Direccion.ms.services.DireccionService;
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
@DisplayName("DireccionService — pruebas unitarias")
class DireccionServiceTest {

    @Mock
    private DireccionRepository direccionRepository;

    //* ComunaRepository también se mockea porque el servicio lo usa para validar la FK
    @Mock
    private ComunaRepository comunaRepository;

    //* @InjectMocks crea una instancia real del servicio e inyecta ambos mocks
    @InjectMocks
    private DireccionService direccionService;

    //* Entidades base reutilizadas en los tests para evitar repetición
    private Direccion direccionBase;
    private Comuna comunaBase;

    @BeforeEach
    void setUp() {
        //* Prepara una comuna de prueba
        comunaBase = new Comuna();
        comunaBase.setId_comuna(1);
        comunaBase.setNombre_comuna("Santiago");

        //* Prepara una dirección asociada a esa comuna
        direccionBase = new Direccion();
        direccionBase.setId_direccion(1);
        direccionBase.setCalle("Av. Providencia");
        direccionBase.setNumero("1234");
        //* La dirección contiene el objeto Comuna completo (JPA maneja la FK internamente)
        direccionBase.setComuna(comunaBase);
    }

    // ── obtenerTodasLasDirecciones ─────────────────────────────────────────────

    @Test
    @DisplayName("obtenerTodasLasDirecciones: retorna lista con todas las direcciones")
    void obtenerTodasLasDirecciones_retornaLista() {
        //* Arrange
        when(direccionRepository.findAll()).thenReturn(List.of(direccionBase));

        //* Act
        List<DireccionDTO> resultado = direccionService.obtenerTodasLasDirecciones();

        //* Assert
        assertEquals(1, resultado.size());
        assertEquals("Av. Providencia", resultado.get(0).calle());
        verify(direccionRepository, times(1)).findAll();
    }

    // ── obtenerDireccionPorId ──────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerDireccionPorId: retorna DTO cuando el ID existe")
    void obtenerDireccionPorId_existente() {
        //* Arrange
        when(direccionRepository.findById(1)).thenReturn(Optional.of(direccionBase));

        //* Act
        DireccionDTO resultado = direccionService.obtenerDireccionPorId(1);

        //* Assert: el DTO expone id_comuna en vez del objeto Comuna completo
        assertNotNull(resultado);
        assertEquals(1, resultado.id_direccion());
        assertEquals(1, resultado.id_comuna());
    }

    @Test
    @DisplayName("obtenerDireccionPorId: lanza 404 si el ID no existe")
    void obtenerDireccionPorId_noExiste_lanza404() {
        //* Arrange
        when(direccionRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> direccionService.obtenerDireccionPorId(99));
    }

    // ── agregarDireccion ───────────────────────────────────────────────────────

    @Test
    @DisplayName("agregarDireccion: guarda correctamente cuando la comuna existe")
    void agregarDireccion_comunaExistente() {
        //* Arrange
        AgregarDireccion req = new AgregarDireccion();
        req.setCalle("Calle Nueva");
        req.setNumero("456");
        req.setId_comuna(1);

        //* El servicio primero valida que la comuna exista antes de crear la dirección
        when(comunaRepository.findById(1)).thenReturn(Optional.of(comunaBase));
        when(direccionRepository.save(any(Direccion.class))).thenReturn(direccionBase);

        //* Act
        DireccionDTO resultado = direccionService.agregarDireccion(req);

        //* Assert: se consultó la comuna y se persistió la dirección
        assertNotNull(resultado);
        verify(comunaRepository, times(1)).findById(1);
        verify(direccionRepository, times(1)).save(any(Direccion.class));
    }

    @Test
    @DisplayName("agregarDireccion: lanza 404 si la comuna referenciada no existe")
    void agregarDireccion_comunaInexistente_lanza404() {
        //* Arrange: se intenta crear una dirección con una comuna que no existe
        AgregarDireccion req = new AgregarDireccion();
        req.setCalle("Calle Nueva");
        req.setNumero("456");
        req.setId_comuna(99);

        //* La comuna 99 no existe → el servicio debe rechazar la creación
        when(comunaRepository.findById(99)).thenReturn(Optional.empty());

        //! HTTP 404 — no se puede crear una dirección con una comuna inexistente
        assertThrows(ResponseStatusException.class,
                () -> direccionService.agregarDireccion(req));
        //* Si la comuna no existe, nunca debe intentarse guardar la dirección
        verify(direccionRepository, never()).save(any());
    }

    // ── actualizarDireccion ────────────────────────────────────────────────────

    @Test
    @DisplayName("actualizarDireccion: actualiza correctamente cuando existen dirección y comuna")
    void actualizarDireccion_actualiza() {
        //* Arrange
        ActualizarDireccion req = new ActualizarDireccion();
        req.setCalle("Nueva Calle");
        req.setNumero("789");
        req.setId_comuna(1);

        //* El servicio carga tanto la dirección como la nueva comuna antes de actualizar
        when(direccionRepository.findById(1)).thenReturn(Optional.of(direccionBase));
        when(comunaRepository.findById(1)).thenReturn(Optional.of(comunaBase));
        when(direccionRepository.save(any(Direccion.class))).thenReturn(direccionBase);

        //* Act
        DireccionDTO resultado = direccionService.actualizarDireccion(1, req);

        //* Assert
        assertNotNull(resultado);
        verify(direccionRepository, times(1)).save(any(Direccion.class));
    }

    @Test
    @DisplayName("actualizarDireccion: lanza 404 si la dirección no existe")
    void actualizarDireccion_noExiste_lanza404() {
        //* Arrange
        ActualizarDireccion req = new ActualizarDireccion();
        req.setCalle("Calle");
        req.setNumero("1");
        req.setId_comuna(1);

        //* La dirección 99 no existe
        when(direccionRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> direccionService.actualizarDireccion(99, req));
    }

    // ── eliminarDireccion ──────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminarDireccion: elimina correctamente cuando existe")
    void eliminarDireccion_existente() {
        //* Arrange
        when(direccionRepository.existsById(1)).thenReturn(true);
        doNothing().when(direccionRepository).deleteById(1);

        //* Act
        String resultado = direccionService.eliminarDireccion(1);

        //* Assert
        assertEquals("Dirección eliminada correctamente.", resultado);
        verify(direccionRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("eliminarDireccion: lanza 404 si no existe")
    void eliminarDireccion_noExiste_lanza404() {
        //* Arrange
        when(direccionRepository.existsById(99)).thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> direccionService.eliminarDireccion(99));
    }

    // ── obtenerDireccionesPorComuna ────────────────────────────────────────────

    @Test
    @DisplayName("obtenerDireccionesPorComuna: filtra por id_comuna correctamente")
    void obtenerDireccionesPorComuna_filtrado() {
        //* Arrange
        when(direccionRepository.findByComuna_IdComuna(1))
                .thenReturn(List.of(direccionBase));

        //* Act
        List<DireccionDTO> resultado =
                direccionService.obtenerDireccionesPorComuna(1);

        //* Assert
        assertEquals(1, resultado.size());
        assertEquals(1, resultado.get(0).id_comuna());
    }
}
