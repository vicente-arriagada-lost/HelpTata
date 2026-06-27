package com.Direccion.ms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Direccion.ms.models.dto.ComunaDTO;
import com.Direccion.ms.models.request.ActualizarComuna;
import com.Direccion.ms.models.request.AgregarComuna;
import com.Direccion.ms.services.ComunaService;

import jakarta.validation.Valid;

//* Controlador REST que expone los endpoints de gestión de comunas
//? @RestController = @Controller + @ResponseBody (responde JSON automáticamente)
//? @RequestMapping define el prefijo de todas las rutas de este controlador
@RestController
@RequestMapping("/api/comunas")
public class ComunaController {

    //* Servicio inyectado que contiene la lógica de negocio
    @Autowired
    private ComunaService comunaService;

    //* GET /api/comunas — retorna todas las comunas como ComunaDTO (con id_ciudad en vez del objeto Ciudad)
    @GetMapping
    public ResponseEntity<List<ComunaDTO>> obtenerTodas() {
        return ResponseEntity.ok(comunaService.obtenerTodasLasComunas());
    }

    //* GET /api/comunas/{id} — retorna una comuna por su ID como ComunaDTO
    //? @PathVariable extrae el valor {id} de la URL
    @GetMapping("/{id}")
    public ResponseEntity<ComunaDTO> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(comunaService.obtenerComunaPorId(id));
    }

    //* GET /api/comunas/ciudad/{idCiudad} — retorna todas las comunas de una ciudad como DTOs
    //? Endpoint en cascada: útil para poblar un select de comunas al elegir una ciudad
    @GetMapping("/ciudad/{idCiudad}")
    public ResponseEntity<List<ComunaDTO>> obtenerPorCiudad(@PathVariable int idCiudad) {
        return ResponseEntity.ok(comunaService.obtenerComunasPorCiudad(idCiudad));
    }

    //* POST /api/comunas — crea una nueva comuna y retorna el ComunaDTO creado
    //? @Valid activa las validaciones del DTO de entrada (ej: @NotBlank, @Positive)
    //? @RequestBody deserializa el JSON del request al DTO
    //! Responde con HTTP 201 Created en lugar del 200 por defecto
    @PostMapping
    public ResponseEntity<ComunaDTO> agregar(@Valid @RequestBody AgregarComuna nuevaComuna) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(comunaService.agregarComuna(nuevaComuna));
    }

    //* PUT /api/comunas/{id} — actualiza los datos de una comuna y retorna el ComunaDTO
    //? El ID viene en el path y los nuevos datos vienen en el body
    @PutMapping("/{id}")
    public ResponseEntity<ComunaDTO> actualizar(@PathVariable int id,
                                                @Valid @RequestBody ActualizarComuna actComuna) {
        return ResponseEntity.ok(comunaService.actualizarComuna(id, actComuna));
    }

    //* DELETE /api/comunas/{id} — elimina una comuna por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        return ResponseEntity.ok(comunaService.eliminarComuna(id));
    }

}
