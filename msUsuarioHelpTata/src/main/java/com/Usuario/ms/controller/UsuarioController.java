package com.Usuario.ms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Usuario.ms.models.dto.LoginResponse;
import com.Usuario.ms.models.dto.UsuarioDTO;
import com.Usuario.ms.models.request.ActualizarUsuario;
import com.Usuario.ms.models.request.AgregarUsuario;
import com.Usuario.ms.models.request.LoginRequest;
import com.Usuario.ms.services.UsuarioService;

import jakarta.validation.Valid;

//* Controlador REST que expone los endpoints de gestión de usuarios
//? @RestController = @Controller + @ResponseBody (responde JSON automáticamente)
//? @RequestMapping define el prefijo de todas las rutas de este controlador
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    //* Servicio inyectado que contiene la lógica de negocio
    @Autowired
    private UsuarioService usuarioService;

    //* GET /api/usuarios — retorna todos los usuarios como UsuarioDTO (sin passwords)
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> obtenerTodos() {
        return ResponseEntity.ok(usuarioService.obtenerTodosLosUsuarios());
    }

    //* GET /api/usuarios/{id} — retorna un usuario por su ID como UsuarioDTO
    //? @PathVariable extrae el valor {id} de la URL
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(usuarioService.obtenerUsuarioPorId(id));
    }

    //* POST /api/usuarios — crea un nuevo usuario y retorna el UsuarioDTO creado
    //? @Valid activa las validaciones del DTO de entrada (ej: @NotBlank, @NotNull)
    //? @RequestBody deserializa el JSON del request al DTO
    //! Responde con HTTP 201 Created en lugar del 200 por defecto
    @PostMapping
    public ResponseEntity<UsuarioDTO> agregar(@Valid @RequestBody AgregarUsuario nuevoUsuario) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.agregarUsuario(nuevoUsuario));
    }

    //* PUT /api/usuarios/{id} — actualiza los datos de un usuario y retorna el UsuarioDTO
    //? El ID viene en el path y los nuevos datos vienen en el body
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizar(@PathVariable int id,
                                                 @Valid @RequestBody ActualizarUsuario actUsuario) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, actUsuario));
    }

    //* DELETE /api/usuarios/{id} — elimina un usuario por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        return ResponseEntity.ok(usuarioService.eliminarUsuario(id));
    }

    //* POST /api/usuarios/login — autentica por email + contraseña
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(usuarioService.login(req));
    }

}
