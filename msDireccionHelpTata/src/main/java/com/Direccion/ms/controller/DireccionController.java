package com.Direccion.ms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Direccion.ms.models.dto.DireccionDTO;
import com.Direccion.ms.models.request.ActualizarDireccion;
import com.Direccion.ms.models.request.AgregarDireccion;
import com.Direccion.ms.services.DireccionService;

import jakarta.validation.Valid;

//* Controlador REST que expone los endpoints de gestión de direcciones
//* Es el controlador principal consumido por el MS Usuario:
//*   - POST /api/direcciones → el MS Usuario llama para crear un domicilio
//*   - GET  /api/direcciones/{id} → el MS Usuario llama para validar que exista
//? @RestController = @Controller + @ResponseBody (responde JSON automáticamente)
//? @RequestMapping define el prefijo de todas las rutas de este controlador
@RestController
@RequestMapping("/api/direcciones")
public class DireccionController {

    //* Servicio inyectado que contiene la lógica de negocio
    @Autowired
    private DireccionService direccionService;

    //* GET /api/direcciones — retorna todas las direcciones como DireccionDTO (con id_comuna, sin objetos anidados)
    @GetMapping
    public ResponseEntity<List<DireccionDTO>> obtenerTodas() {
        return ResponseEntity.ok(direccionService.obtenerTodasLasDirecciones());
    }

    //* GET /api/direcciones/{id} — retorna una dirección por su ID como DireccionDTO
    //? @PathVariable extrae el valor {id} de la URL
    @GetMapping("/{id}")
    public ResponseEntity<DireccionDTO> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(direccionService.obtenerDireccionPorId(id));
    }

    //* GET /api/direcciones/comuna/{idComuna} — retorna todas las direcciones de una comuna como DTOs
    @GetMapping("/comuna/{idComuna}")
    public ResponseEntity<List<DireccionDTO>> obtenerPorComuna(@PathVariable int idComuna) {
        return ResponseEntity.ok(direccionService.obtenerDireccionesPorComuna(idComuna));
    }

    //* POST /api/direcciones — crea una dirección y retorna el DireccionDTO creado
    //? @Valid activa las validaciones del DTO de entrada (ej: @NotBlank, @Positive)
    //? @RequestBody deserializa el JSON del request al DTO
    //! Responde con HTTP 201 Created en lugar del 200 por defecto
    //! El id_direccion del DTO retornado es el que el MS Usuario debe guardar en su tabla
    @PostMapping
    public ResponseEntity<DireccionDTO> agregar(@Valid @RequestBody AgregarDireccion nuevaDireccion) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(direccionService.agregarDireccion(nuevaDireccion));
    }

    //* PUT /api/direcciones/{id} — actualiza los datos de una dirección y retorna el DireccionDTO
    //? El ID viene en el path y los nuevos datos vienen en el body
    @PutMapping("/{id}")
    public ResponseEntity<DireccionDTO> actualizar(@PathVariable int id,
                                                   @Valid @RequestBody ActualizarDireccion actDireccion) {
        return ResponseEntity.ok(direccionService.actualizarDireccion(id, actDireccion));
    }

    //* DELETE /api/direcciones/{id} — elimina una dirección por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        return ResponseEntity.ok(direccionService.eliminarDireccion(id));
    }

}
