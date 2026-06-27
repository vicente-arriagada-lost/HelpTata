package com.Tutoriales.ms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Tutoriales.ms.models.dto.TutorialDTO;
import com.Tutoriales.ms.models.request.ActualizarTutorial;
import com.Tutoriales.ms.models.request.AgregarTutorial;
import com.Tutoriales.ms.services.TutorialService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tutoriales")
public class TutorialController {

    @Autowired
    private TutorialService tutorialService;

    @GetMapping
    public ResponseEntity<List<TutorialDTO>> obtenerTodos() {
        return ResponseEntity.ok(tutorialService.obtenerTodosLosTutoriales());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TutorialDTO> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(tutorialService.obtenerTutorialPorId(id));
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<TutorialDTO>> obtenerPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(tutorialService.obtenerTutorialesPorCategoria(categoria));
    }

    @GetMapping("/nivel/{nivel}")
    public ResponseEntity<List<TutorialDTO>> obtenerPorNivel(@PathVariable String nivel) {
        return ResponseEntity.ok(tutorialService.obtenerTutorialesPorNivel(nivel));
    }

    @PostMapping
    public ResponseEntity<TutorialDTO> agregar(@Valid @RequestBody AgregarTutorial nuevoTutorial) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tutorialService.agregarTutorial(nuevoTutorial));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TutorialDTO> actualizar(@PathVariable int id,
                                                   @Valid @RequestBody ActualizarTutorial actTutorial) {
        return ResponseEntity.ok(tutorialService.actualizarTutorial(id, actTutorial));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        return ResponseEntity.ok(tutorialService.eliminarTutorial(id));
    }
}
