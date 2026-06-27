package com.Tutoriales.ms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Tutoriales.ms.models.dto.FotoDTO;
import com.Tutoriales.ms.models.request.ActualizarFoto;
import com.Tutoriales.ms.models.request.AgregarFoto;
import com.Tutoriales.ms.services.FotoService;

import jakarta.validation.Valid;

//* Controlador REST que expone los endpoints de gestión de fotos de tutoriales
//? @RestController = @Controller + @ResponseBody (responde JSON automáticamente)
//? @RequestMapping define el prefijo de todas las rutas de este controlador
@RestController
@RequestMapping("/api/fotos")
public class FotoController {

    //* Servicio inyectado que contiene la lógica de negocio
    @Autowired
    private FotoService fotoService;

    //* GET /api/fotos — retorna todas las fotos como FotoDTO (con id_tutor, sin el objeto Tutorial)
    @GetMapping
    public ResponseEntity<List<FotoDTO>> obtenerTodas() {
        return ResponseEntity.ok(fotoService.obtenerTodasLasFotos());
    }

    //* GET /api/fotos/{id} — retorna una foto por su ID como FotoDTO
    //? @PathVariable extrae el valor {id} de la URL
    @GetMapping("/{id}")
    public ResponseEntity<FotoDTO> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(fotoService.obtenerFotoPorId(id));
    }

    //* GET /api/fotos/tutorial/{idTutor} — retorna todas las fotos de un tutorial como DTOs
    //? Útil para cargar la galería completa de un tutorial en el frontend
    @GetMapping("/tutorial/{idTutor}")
    public ResponseEntity<List<FotoDTO>> obtenerPorTutorial(@PathVariable int idTutor) {
        return ResponseEntity.ok(fotoService.obtenerFotosPorTutorial(idTutor));
    }

    //* POST /api/fotos — agrega una nueva foto y retorna el FotoDTO creado
    //? @Valid activa las validaciones del DTO de entrada (ej: @NotBlank, @Positive)
    //? @RequestBody deserializa el JSON del request al DTO
    //! Responde con HTTP 201 Created en lugar del 200 por defecto
    @PostMapping
    public ResponseEntity<FotoDTO> agregar(@Valid @RequestBody AgregarFoto nuevaFoto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fotoService.agregarFoto(nuevaFoto));
    }

    //* PUT /api/fotos/{id} — actualiza el contenido de una foto y retorna el FotoDTO
    //? El ID viene en el path y los nuevos datos vienen en el body
    @PutMapping("/{id}")
    public ResponseEntity<FotoDTO> actualizar(@PathVariable int id,
                                              @Valid @RequestBody ActualizarFoto actFoto) {
        return ResponseEntity.ok(fotoService.actualizarFoto(id, actFoto));
    }

    //* DELETE /api/fotos/{id} — elimina una foto por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        return ResponseEntity.ok(fotoService.eliminarFoto(id));
    }

}
