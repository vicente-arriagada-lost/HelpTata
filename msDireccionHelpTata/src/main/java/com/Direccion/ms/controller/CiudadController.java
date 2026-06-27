package com.Direccion.ms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Direccion.ms.models.dto.CiudadDTO;
import com.Direccion.ms.models.request.ActualizarCiudad;
import com.Direccion.ms.models.request.AgregarCiudad;
import com.Direccion.ms.services.CiudadService;

import jakarta.validation.Valid;

//* Controlador REST que expone los endpoints de gestión de ciudades
//? @RestController = @Controller + @ResponseBody (responde JSON automáticamente)
//? @RequestMapping define el prefijo de todas las rutas de este controlador
@RestController
@RequestMapping("/api/ciudades")
public class CiudadController {

    //* Servicio inyectado que contiene la lógica de negocio
    @Autowired
    private CiudadService ciudadService;

    //* GET /api/ciudades — retorna todas las ciudades como CiudadDTO (con id_region en vez del objeto Region)
    @GetMapping
    public ResponseEntity<List<CiudadDTO>> obtenerTodas() {
        return ResponseEntity.ok(ciudadService.obtenerTodasLasCiudades());
    }

    //* GET /api/ciudades/{id} — retorna una ciudad por su ID como CiudadDTO
    //? @PathVariable extrae el valor {id} de la URL
    @GetMapping("/{id}")
    public ResponseEntity<CiudadDTO> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(ciudadService.obtenerCiudadPorId(id));
    }

    //* GET /api/ciudades/region/{idRegion} — retorna todas las ciudades de una región como DTOs
    //? Endpoint en cascada: útil para poblar un select de ciudades al elegir una región
    @GetMapping("/region/{idRegion}")
    public ResponseEntity<List<CiudadDTO>> obtenerPorRegion(@PathVariable int idRegion) {
        return ResponseEntity.ok(ciudadService.obtenerCiudadesPorRegion(idRegion));
    }

    //* POST /api/ciudades — crea una nueva ciudad y retorna el CiudadDTO creado
    //? @Valid activa las validaciones del DTO de entrada (ej: @NotBlank, @Positive)
    //? @RequestBody deserializa el JSON del request al DTO
    //! Responde con HTTP 201 Created en lugar del 200 por defecto
    @PostMapping
    public ResponseEntity<CiudadDTO> agregar(@Valid @RequestBody AgregarCiudad nuevaCiudad) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ciudadService.agregarCiudad(nuevaCiudad));
    }

    //* PUT /api/ciudades/{id} — actualiza los datos de una ciudad y retorna el CiudadDTO
    //? El ID viene en el path y los nuevos datos vienen en el body
    @PutMapping("/{id}")
    public ResponseEntity<CiudadDTO> actualizar(@PathVariable int id,
                                                @Valid @RequestBody ActualizarCiudad actCiudad) {
        return ResponseEntity.ok(ciudadService.actualizarCiudad(id, actCiudad));
    }

    //* DELETE /api/ciudades/{id} — elimina una ciudad por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        return ResponseEntity.ok(ciudadService.eliminarCiudad(id));
    }

}
