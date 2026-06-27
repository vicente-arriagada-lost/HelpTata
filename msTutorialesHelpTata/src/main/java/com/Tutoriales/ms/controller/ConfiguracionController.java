package com.Tutoriales.ms.controller;

import com.Tutoriales.ms.services.ConfiguracionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//* Controlador REST para leer configuraciones globales del sistema.
//* Endpoint: GET /api/config/{clave}
//* Ejemplo:  GET /api/config/url_video_tutorial → "http://servidor/media/tutorial.mp4"
@RestController
@RequestMapping("/api/config")
public class ConfiguracionController {

    @Autowired
    private ConfiguracionService configuracionService;

    @GetMapping("/{clave}")
    public ResponseEntity<String> obtenerValor(@PathVariable String clave) {
        return ResponseEntity.ok(configuracionService.obtenerValor(clave));
    }
}
