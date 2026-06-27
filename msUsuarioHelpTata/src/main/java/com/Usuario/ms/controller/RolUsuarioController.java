package com.Usuario.ms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Usuario.ms.models.dto.RolDTO;
import com.Usuario.ms.models.request.ActualizarRol;
import com.Usuario.ms.models.request.AgregarRol;
import com.Usuario.ms.services.RolUsuarioService;

import jakarta.validation.Valid;

//* Controlador REST que expone los endpoints de gestión de roles
//? Todas las rutas parten de /api/roles
@RestController
@RequestMapping("/api/roles")
public class RolUsuarioController {

    //* Servicio inyectado con la lógica de negocio de roles
    @Autowired
    private RolUsuarioService rolUsuarioService;

    //* GET /api/roles — retorna todos los roles como RolDTO
    @GetMapping
    public ResponseEntity<List<RolDTO>> obtenerTodos() {
        return ResponseEntity.ok(rolUsuarioService.obtenerTodosLosRoles());
    }

    //* GET /api/roles/{id} — retorna un rol por su ID como RolDTO
    @GetMapping("/{id}")
    public ResponseEntity<RolDTO> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(rolUsuarioService.obtenerRolPorId(id));
    }

    //* POST /api/roles — crea un nuevo rol y retorna el RolDTO creado
    //! Responde con HTTP 201 Created
    @PostMapping
    public ResponseEntity<RolDTO> agregar(@Valid @RequestBody AgregarRol nuevoRol) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rolUsuarioService.agregarRol(nuevoRol));
    }

    //* PUT /api/roles/{id} — actualiza el tipo de un rol y retorna el RolDTO
    @PutMapping("/{id}")
    public ResponseEntity<RolDTO> actualizar(@PathVariable int id,
                                             @Valid @RequestBody ActualizarRol actRol) {
        return ResponseEntity.ok(rolUsuarioService.actualizarRol(id, actRol));
    }

    //* DELETE /api/roles/{id} — elimina un rol por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        return ResponseEntity.ok(rolUsuarioService.eliminarRol(id));
    }

}
