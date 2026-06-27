package com.Direccion.ms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Direccion.ms.models.dto.RegionDTO;
import com.Direccion.ms.models.request.ActualizarRegion;
import com.Direccion.ms.models.request.AgregarRegion;
import com.Direccion.ms.services.RegionService;

import jakarta.validation.Valid;

//* Controlador REST que expone los endpoints de gestión de regiones
//? @RestController = @Controller + @ResponseBody (responde JSON automáticamente)
//? @RequestMapping define el prefijo de todas las rutas de este controlador
@RestController
@RequestMapping("/api/regiones")
public class RegionController {

    //* Servicio inyectado que contiene la lógica de negocio
    @Autowired
    private RegionService regionService;

    //* GET /api/regiones — retorna todas las regiones como RegionDTO (con id_pais en vez del objeto Pais)
    @GetMapping
    public ResponseEntity<List<RegionDTO>> obtenerTodas() {
        return ResponseEntity.ok(regionService.obtenerTodasLasRegiones());
    }

    //* GET /api/regiones/{id} — retorna una región por su ID como RegionDTO
    //? @PathVariable extrae el valor {id} de la URL
    @GetMapping("/{id}")
    public ResponseEntity<RegionDTO> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(regionService.obtenerRegionPorId(id));
    }

    //* GET /api/regiones/pais/{idPais} — retorna todas las regiones de un país como DTOs
    //? Endpoint en cascada: útil para poblar un select de regiones al elegir un país
    @GetMapping("/pais/{idPais}")
    public ResponseEntity<List<RegionDTO>> obtenerPorPais(@PathVariable int idPais) {
        return ResponseEntity.ok(regionService.obtenerRegionesPorPais(idPais));
    }

    //* POST /api/regiones — crea una nueva región y retorna el RegionDTO creado
    //? @Valid activa las validaciones del DTO de entrada (ej: @NotBlank, @Positive)
    //? @RequestBody deserializa el JSON del request al DTO
    //! Responde con HTTP 201 Created en lugar del 200 por defecto
    @PostMapping
    public ResponseEntity<RegionDTO> agregar(@Valid @RequestBody AgregarRegion nuevaRegion) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(regionService.agregarRegion(nuevaRegion));
    }

    //* PUT /api/regiones/{id} — actualiza los datos de una región y retorna el RegionDTO
    //? El ID viene en el path y los nuevos datos vienen en el body
    @PutMapping("/{id}")
    public ResponseEntity<RegionDTO> actualizar(@PathVariable int id,
                                                @Valid @RequestBody ActualizarRegion actRegion) {
        return ResponseEntity.ok(regionService.actualizarRegion(id, actRegion));
    }

    //* DELETE /api/regiones/{id} — elimina una región por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        return ResponseEntity.ok(regionService.eliminarRegion(id));
    }

}
