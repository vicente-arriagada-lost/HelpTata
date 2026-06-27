package com.Logs.ms;

// =============================================================
// TESTS UNITARIOS — LogServiceTest.java
// =============================================================
// Pruebas de la lógica de negocio de LogService usando Mockito.
// No se necesita base de datos: LogRepository es un mock.
//
// Caso especial probado:
//   agregarLog_asignaFechaAutomatica — verifica que fecha_hora_log
//   la asigna el servicio (LocalDateTime.now()), no el request.
//   Esto garantiza que el timestamp del evento sea el real, no
//   uno enviado por el cliente.
//
//   actualizarLog_conservaFechaOriginal — el timestamp no debe
//   modificarse al actualizar un log (preserva el momento exacto
//   en que ocurrió el evento).
//
// Ejecutar con: ./mvnw test
// =============================================================

import com.Logs.ms.models.dto.LogDTO;
import com.Logs.ms.models.entities.Log;
import com.Logs.ms.models.request.AgregarLog;
import com.Logs.ms.models.request.ActualizarLog;
import com.Logs.ms.repositories.LogRepository;
import com.Logs.ms.services.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//* @ExtendWith habilita la integración de Mockito con JUnit 5
@ExtendWith(MockitoExtension.class)
@DisplayName("LogService — pruebas unitarias")
class LogServiceTest {

    @Mock
    private LogRepository logRepository;

    //* @InjectMocks crea una instancia real del servicio e inyecta el mock
    @InjectMocks
    private LogService logService;

    //* Log base reutilizado en los tests para evitar repetición
    private Log logBase;

    @BeforeEach
    void setUp() {
        //* Prepara un log de tipo INFO con todos los campos
        logBase = new Log();
        logBase.setId_log(1);
        logBase.setTipo_log("INFO");
        logBase.setServicio_origen("msUsuario");
        logBase.setMensaje_log("Usuario autenticado correctamente.");
        logBase.setFecha_hora_log(LocalDateTime.now());
        logBase.setId_usuario(1);
        logBase.setIp_log("127.0.0.1");
        logBase.setDetalle_log("Login exitoso para usuario@test.com");
    }

    // ── obtenerTodosLosLogs ────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerTodosLosLogs: retorna lista con todos los logs")
    void obtenerTodosLosLogs_retornaLista() {
        //* Arrange
        when(logRepository.findAll()).thenReturn(List.of(logBase));

        //* Act
        List<LogDTO> resultado = logService.obtenerTodosLosLogs();

        //* Assert
        assertEquals(1, resultado.size());
        assertEquals("INFO", resultado.get(0).tipo_log());
        verify(logRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("obtenerTodosLosLogs: retorna lista vacía si no hay logs")
    void obtenerTodosLosLogs_listaVacia() {
        //* Arrange: BD sin registros
        when(logRepository.findAll()).thenReturn(List.of());

        assertTrue(logService.obtenerTodosLosLogs().isEmpty());
    }

    // ── obtenerLogPorId ────────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerLogPorId: retorna DTO cuando el ID existe")
    void obtenerLogPorId_existente() {
        //* Arrange
        when(logRepository.findById(1)).thenReturn(Optional.of(logBase));

        //* Act
        LogDTO resultado = logService.obtenerLogPorId(1);

        //* Assert: verifica campos clave del DTO
        assertNotNull(resultado);
        assertEquals(1, resultado.id_log());
        assertEquals("msUsuario", resultado.servicio_origen());
    }

    @Test
    @DisplayName("obtenerLogPorId: lanza 404 si el ID no existe")
    void obtenerLogPorId_noExiste_lanza404() {
        //* Arrange
        when(logRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> logService.obtenerLogPorId(99));
    }

    // ── obtenerLogsPorTipo ─────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerLogsPorTipo: filtra correctamente por tipo 'ERROR'")
    void obtenerLogsPorTipo_filtrado() {
        //* Arrange: log separado de tipo ERROR
        Log logError = new Log();
        logError.setId_log(2);
        logError.setTipo_log("ERROR");
        logError.setServicio_origen("msTutoriales");
        logError.setMensaje_log("Tutorial no encontrado.");
        logError.setFecha_hora_log(LocalDateTime.now());

        when(logRepository.findByTipoLog("ERROR")).thenReturn(List.of(logError));

        //* Act
        List<LogDTO> resultado = logService.obtenerLogsPorTipo("ERROR");

        //* Assert
        assertEquals(1, resultado.size());
        assertEquals("ERROR", resultado.get(0).tipo_log());
    }

    // ── obtenerLogsPorUsuario ──────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerLogsPorUsuario: retorna logs del usuario con ID 1")
    void obtenerLogsPorUsuario_filtrado() {
        //* Arrange
        when(logRepository.findByIdUsuario(1)).thenReturn(List.of(logBase));

        //* Act
        List<LogDTO> resultado = logService.obtenerLogsPorUsuario(1);

        //* Assert
        assertEquals(1, resultado.size());
        assertEquals(1, resultado.get(0).id_usuario());
    }

    // ── agregarLog ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("agregarLog: asigna fecha_hora_log automáticamente (no viene del request)")
    void agregarLog_asignaFechaAutomatica() {
        //* Arrange: el request NO incluye fecha — el servicio la asigna
        AgregarLog req = new AgregarLog();
        req.setTipo_log("WARN");
        req.setServicio_origen("msProgreso");
        req.setMensaje_log("Registro duplicado detectado.");
        req.setId_usuario(2);
        req.setIp_log("192.168.1.1");
        req.setDetalle_log("Detalle adicional.");

        when(logRepository.save(any(Log.class))).thenAnswer(inv -> {
            Log log = inv.getArgument(0);
            //* La fecha debe haber sido asignada por el servicio, no por el request
            assertNotNull(log.getFecha_hora_log());
            return logBase;
        });

        //* Act
        LogDTO resultado = logService.agregarLog(req);

        //* Assert
        assertNotNull(resultado);
        verify(logRepository, times(1)).save(any(Log.class));
    }

    // ── eliminarLog ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminarLog: elimina correctamente cuando el log existe")
    void eliminarLog_existente() {
        //* Arrange
        when(logRepository.existsById(1)).thenReturn(true);
        doNothing().when(logRepository).deleteById(1);

        //* Act
        String resultado = logService.eliminarLog(1);

        //* Assert
        assertEquals("Log eliminado correctamente.", resultado);
        verify(logRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("eliminarLog: lanza 404 si el log no existe")
    void eliminarLog_noExiste_lanza404() {
        //* Arrange
        when(logRepository.existsById(99)).thenReturn(false);

        //! Si no existe, nunca debe llamarse deleteById
        assertThrows(ResponseStatusException.class,
                () -> logService.eliminarLog(99));
        verify(logRepository, never()).deleteById(anyInt());
    }

    // ── actualizarLog ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("actualizarLog: no modifica fecha_hora_log original")
    void actualizarLog_conservaFechaOriginal() {
        //* Arrange: log con fecha fija del pasado
        LocalDateTime fechaOriginal = LocalDateTime.of(2024, 1, 15, 10, 30);
        logBase.setFecha_hora_log(fechaOriginal);

        ActualizarLog req = new ActualizarLog();
        req.setTipo_log("ERROR");
        req.setServicio_origen("msUsuario");
        req.setMensaje_log("Mensaje actualizado.");
        req.setId_usuario(1);
        req.setIp_log("127.0.0.1");
        req.setDetalle_log("Detalle actualizado.");

        when(logRepository.findById(1)).thenReturn(Optional.of(logBase));
        when(logRepository.save(any(Log.class))).thenAnswer(inv -> {
            Log log = inv.getArgument(0);
            //! El timestamp original no debe modificarse al actualizar el contenido del log
            assertEquals(fechaOriginal, log.getFecha_hora_log());
            return log;
        });

        //* Act
        logService.actualizarLog(1, req);
    }
}
