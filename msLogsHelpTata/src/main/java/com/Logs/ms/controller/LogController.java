package com.Logs.ms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Logs.ms.models.dto.LogDTO;
import com.Logs.ms.models.request.ActualizarLog;
import com.Logs.ms.models.request.AgregarLog;
import com.Logs.ms.services.LogService;

import jakarta.validation.Valid;

//* Controlador REST que expone los endpoints de gestión de logs del sistema
//? @RestController = @Controller + @ResponseBody (responde JSON automáticamente)
//? @RequestMapping define el prefijo de todas las rutas de este controlador
@RestController
@RequestMapping("/api/logs")
public class LogController {

    //* Servicio inyectado que contiene la lógica de negocio
    @Autowired
    private LogService logService;

    //* GET /api/logs — retorna todos los logs como LogDTO
    @GetMapping
    public ResponseEntity<List<LogDTO>> obtenerTodos() {
        return ResponseEntity.ok(logService.obtenerTodosLosLogs());
    }

    //* GET /api/logs/{id} — retorna un log por su ID como LogDTO
    //? @PathVariable extrae el valor {id} de la URL
    @GetMapping("/{id}")
    public ResponseEntity<LogDTO> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(logService.obtenerLogPorId(id));
    }

    //* GET /api/logs/tipo/{tipo} — retorna logs filtrados por tipo como DTOs (ej: "ERROR")
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<LogDTO>> obtenerPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(logService.obtenerLogsPorTipo(tipo));
    }

    //* GET /api/logs/usuario/{idUsuario} — retorna logs filtrados por usuario como DTOs
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<LogDTO>> obtenerPorUsuario(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(logService.obtenerLogsPorUsuario(idUsuario));
    }

    //* GET /api/logs/servicio/{servicio} — retorna logs filtrados por microservicio como DTOs
    @GetMapping("/servicio/{servicio}")
    public ResponseEntity<List<LogDTO>> obtenerPorServicio(@PathVariable String servicio) {
        return ResponseEntity.ok(logService.obtenerLogsPorServicio(servicio));
    }

    //* POST /api/logs — registra un nuevo log y retorna el LogDTO creado
    //? @Valid activa las validaciones del DTO de entrada (ej: @NotBlank)
    //? @RequestBody deserializa el JSON del request al DTO
    //! Responde con HTTP 201 Created en lugar del 200 por defecto
    @PostMapping
    public ResponseEntity<LogDTO> agregar(@Valid @RequestBody AgregarLog nuevoLog) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(logService.agregarLog(nuevoLog));
    }

    //* PUT /api/logs/{id} — actualiza un log y retorna el LogDTO
    //? El ID viene en el path y los nuevos datos vienen en el body
    @PutMapping("/{id}")
    public ResponseEntity<LogDTO> actualizar(@PathVariable int id,
                                             @Valid @RequestBody ActualizarLog actLog) {
        return ResponseEntity.ok(logService.actualizarLog(id, actLog));
    }

    //* DELETE /api/logs/{id} — elimina un log por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        return ResponseEntity.ok(logService.eliminarLog(id));
    }

}
