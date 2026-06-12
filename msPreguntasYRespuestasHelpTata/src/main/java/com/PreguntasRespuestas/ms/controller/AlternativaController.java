package com.PreguntasRespuestas.ms.controller;

import com.PreguntasRespuestas.ms.models.dto.AlternativaDTO;
import com.PreguntasRespuestas.ms.models.request.ActualizarAlternativa;
import com.PreguntasRespuestas.ms.models.request.AgregarAlternativa;
import com.PreguntasRespuestas.ms.services.AlternativaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alternativas")
public class AlternativaController {

    @Autowired
    private AlternativaService alternativaService;

    @GetMapping
    public ResponseEntity<List<AlternativaDTO>> obtenerTodas() {
        return ResponseEntity.ok(alternativaService.obtenerTodasLasAlternativas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlternativaDTO> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(alternativaService.obtenerAlternativaPorId(id));
    }

    @GetMapping("/pregunta/{idPregunta}")
    public ResponseEntity<List<AlternativaDTO>> obtenerPorPregunta(@PathVariable int idPregunta) {
        return ResponseEntity.ok(alternativaService.obtenerAlternativasPorPregunta(idPregunta));
    }

    @PostMapping
    public ResponseEntity<AlternativaDTO> agregar(@Valid @RequestBody AgregarAlternativa request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alternativaService.agregarAlternativa(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlternativaDTO> actualizar(@PathVariable int id, @Valid @RequestBody ActualizarAlternativa request) {
        return ResponseEntity.ok(alternativaService.actualizarAlternativa(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        return ResponseEntity.ok(alternativaService.eliminarAlternativa(id));
    }
}
