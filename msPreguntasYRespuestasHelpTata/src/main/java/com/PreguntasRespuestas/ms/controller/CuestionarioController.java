package com.PreguntasRespuestas.ms.controller;

import com.PreguntasRespuestas.ms.models.dto.CuestionarioDTO;
import com.PreguntasRespuestas.ms.models.dto.ResultadoSubmisionDTO;
import com.PreguntasRespuestas.ms.models.request.ActualizarCuestionario;
import com.PreguntasRespuestas.ms.models.request.AgregarCuestionario;
import com.PreguntasRespuestas.ms.models.request.SubmitirCuestionario;
import com.PreguntasRespuestas.ms.services.CuestionarioService;
import com.PreguntasRespuestas.ms.services.ResultadoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuestionarios")
public class CuestionarioController {

    @Autowired
    private CuestionarioService cuestionarioService;

    @Autowired
    private ResultadoService resultadoService;

    @GetMapping
    public ResponseEntity<List<CuestionarioDTO>> obtenerTodos() {
        return ResponseEntity.ok(cuestionarioService.obtenerTodosLosCuestionarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuestionarioDTO> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(cuestionarioService.obtenerCuestionarioPorId(id));
    }

    @GetMapping("/tutorial/{idTutor}")
    public ResponseEntity<List<CuestionarioDTO>> obtenerPorTutorial(@PathVariable int idTutor) {
        return ResponseEntity.ok(cuestionarioService.obtenerCuestionariosPorTutorial(idTutor));
    }

    @PostMapping
    public ResponseEntity<CuestionarioDTO> agregar(@Valid @RequestBody AgregarCuestionario request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cuestionarioService.agregarCuestionario(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuestionarioDTO> actualizar(@PathVariable int id, @Valid @RequestBody ActualizarCuestionario request) {
        return ResponseEntity.ok(cuestionarioService.actualizarCuestionario(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        return ResponseEntity.ok(cuestionarioService.eliminarCuestionario(id));
    }

    // Endpoint principal: el usuario envía sus respuestas y obtiene corrección inmediata
    @PostMapping("/{id}/responder")
    public ResponseEntity<ResultadoSubmisionDTO> responder(@PathVariable int id, @Valid @RequestBody SubmitirCuestionario request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resultadoService.submitirCuestionario(id, request));
    }
}
