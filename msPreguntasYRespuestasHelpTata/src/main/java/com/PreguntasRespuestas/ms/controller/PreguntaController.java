package com.PreguntasRespuestas.ms.controller;

import com.PreguntasRespuestas.ms.models.dto.PreguntaDTO;
import com.PreguntasRespuestas.ms.models.request.ActualizarPregunta;
import com.PreguntasRespuestas.ms.models.request.AgregarPregunta;
import com.PreguntasRespuestas.ms.services.PreguntaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/preguntas")
public class PreguntaController {

    @Autowired
    private PreguntaService preguntaService;

    @GetMapping
    public ResponseEntity<List<PreguntaDTO>> obtenerTodas() {
        return ResponseEntity.ok(preguntaService.obtenerTodasLasPreguntas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PreguntaDTO> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(preguntaService.obtenerPreguntaPorId(id));
    }

    @GetMapping("/cuestionario/{idCuestionario}")
    public ResponseEntity<List<PreguntaDTO>> obtenerPorCuestionario(@PathVariable int idCuestionario) {
        return ResponseEntity.ok(preguntaService.obtenerPreguntasPorCuestionario(idCuestionario));
    }

    @PostMapping
    public ResponseEntity<PreguntaDTO> agregar(@Valid @RequestBody AgregarPregunta request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(preguntaService.agregarPregunta(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PreguntaDTO> actualizar(@PathVariable int id, @Valid @RequestBody ActualizarPregunta request) {
        return ResponseEntity.ok(preguntaService.actualizarPregunta(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        return ResponseEntity.ok(preguntaService.eliminarPregunta(id));
    }
}
