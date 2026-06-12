package com.PreguntasRespuestas.ms.controller;

import com.PreguntasRespuestas.ms.models.dto.ResultadoCuestionarioDTO;
import com.PreguntasRespuestas.ms.services.ResultadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resultados")
public class ResultadoController {

    @Autowired
    private ResultadoService resultadoService;

    @GetMapping
    public ResponseEntity<List<ResultadoCuestionarioDTO>> obtenerTodos() {
        return ResponseEntity.ok(resultadoService.obtenerTodosLosResultados());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultadoCuestionarioDTO> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(resultadoService.obtenerResultadoPorId(id));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ResultadoCuestionarioDTO>> obtenerPorUsuario(@PathVariable int idUsuario) {
        return ResponseEntity.ok(resultadoService.obtenerResultadosPorUsuario(idUsuario));
    }

    @GetMapping("/cuestionario/{idCuestionario}")
    public ResponseEntity<List<ResultadoCuestionarioDTO>> obtenerPorCuestionario(@PathVariable int idCuestionario) {
        return ResponseEntity.ok(resultadoService.obtenerResultadosPorCuestionario(idCuestionario));
    }

    @GetMapping("/usuario/{idUsuario}/cuestionario/{idCuestionario}")
    public ResponseEntity<List<ResultadoCuestionarioDTO>> obtenerHistorial(@PathVariable int idUsuario, @PathVariable int idCuestionario) {
        return ResponseEntity.ok(resultadoService.obtenerHistorialUsuarioEnCuestionario(idUsuario, idCuestionario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        return ResponseEntity.ok(resultadoService.eliminarResultado(id));
    }
}
