package com.Evaluaciones.ms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Evaluaciones.ms.models.dto.EvaluacionDTO;
import com.Evaluaciones.ms.models.request.ActualizarEvaluacion;
import com.Evaluaciones.ms.models.request.AgregarEvaluacion;
import com.Evaluaciones.ms.services.EvaluacionService;

import jakarta.validation.Valid;

//* Controlador REST que expone los endpoints de gestión de evaluaciones
//? @RestController = @Controller + @ResponseBody (responde JSON automáticamente)
//? @RequestMapping define el prefijo de todas las rutas de este controlador
@RestController
@RequestMapping("/api/evaluaciones")
public class EvaluacionController {

    //* Servicio inyectado que contiene la lógica de negocio
    @Autowired
    private EvaluacionService evaluacionService;

    //* GET /api/evaluaciones — retorna todas las evaluaciones como EvaluacionDTO
    @GetMapping
    public ResponseEntity<List<EvaluacionDTO>> obtenerTodas() {
        return ResponseEntity.ok(evaluacionService.obtenerTodasLasEvaluaciones());
    }

    //* GET /api/evaluaciones/{id} — retorna una evaluación por su ID como EvaluacionDTO
    //? @PathVariable extrae el valor {id} de la URL
    @GetMapping("/{id}")
    public ResponseEntity<EvaluacionDTO> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(evaluacionService.obtenerEvaluacionPorId(id));
    }

    //* GET /api/evaluaciones/tutorial/{idTutor} — retorna las evaluaciones de un tutorial como DTOs
    //? Útil para mostrar todas las pruebas disponibles al acceder a un tutorial
    @GetMapping("/tutorial/{idTutor}")
    public ResponseEntity<List<EvaluacionDTO>> obtenerPorTutorial(@PathVariable int idTutor) {
        return ResponseEntity.ok(evaluacionService.obtenerEvaluacionesPorTutorial(idTutor));
    }

    //* GET /api/evaluaciones/nivel/{nivel} — filtra evaluaciones por nivel de dificultad como DTOs
    //? Valores esperados: "BASICO", "INTERMEDIO", "AVANZADO"
    @GetMapping("/nivel/{nivel}")
    public ResponseEntity<List<EvaluacionDTO>> obtenerPorNivel(@PathVariable String nivel) {
        return ResponseEntity.ok(evaluacionService.obtenerEvaluacionesPorNivel(nivel));
    }

    //* GET /api/evaluaciones/tipo/{tipo} — filtra evaluaciones por tipo como DTOs
    //? Valores esperados: "QUIZ", "EXAMEN", "PRACTICA", "AUTOEVALUACION"
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<EvaluacionDTO>> obtenerPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(evaluacionService.obtenerEvaluacionesPorTipo(tipo));
    }

    //* POST /api/evaluaciones — crea una nueva evaluación y retorna el EvaluacionDTO creado
    //? @Valid activa las validaciones del DTO de entrada (ej: @NotBlank, @Positive)
    //? @RequestBody deserializa el JSON del request al DTO
    //! Responde con HTTP 201 Created en lugar del 200 por defecto
    @PostMapping
    public ResponseEntity<EvaluacionDTO> agregar(@Valid @RequestBody AgregarEvaluacion nuevaEvaluacion) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(evaluacionService.agregarEvaluacion(nuevaEvaluacion));
    }

    //* PUT /api/evaluaciones/{id} — actualiza los datos de una evaluación y retorna el EvaluacionDTO
    //? El ID viene en el path y los nuevos datos vienen en el body
    @PutMapping("/{id}")
    public ResponseEntity<EvaluacionDTO> actualizar(@PathVariable int id,
                                                    @Valid @RequestBody ActualizarEvaluacion actEvaluacion) {
        return ResponseEntity.ok(evaluacionService.actualizarEvaluacion(id, actEvaluacion));
    }

    //* DELETE /api/evaluaciones/{id} — elimina una evaluación por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        return ResponseEntity.ok(evaluacionService.eliminarEvaluacion(id));
    }

}
