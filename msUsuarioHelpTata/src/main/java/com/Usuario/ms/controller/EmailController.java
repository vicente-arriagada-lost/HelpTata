package com.Usuario.ms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Usuario.ms.models.dto.EmailDTO;
import com.Usuario.ms.models.request.ActualizarEmail;
import com.Usuario.ms.models.request.AgregarEmail;
import com.Usuario.ms.services.EmailService;

import jakarta.validation.Valid;

//* Controlador REST que expone los endpoints de gestión de emails
//? Todas las rutas parten de /api/emails
@RestController
@RequestMapping("/api/emails")
public class EmailController {

    //* Servicio inyectado con la lógica de negocio de emails
    @Autowired
    private EmailService emailService;

    //* GET /api/emails — retorna todos los emails como EmailDTO
    @GetMapping
    public ResponseEntity<List<EmailDTO>> obtenerTodos() {
        return ResponseEntity.ok(emailService.obtenerTodosLosEmail());
    }

    //* GET /api/emails/{id} — retorna un email por su ID como EmailDTO
    @GetMapping("/{id}")
    public ResponseEntity<EmailDTO> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(emailService.obtenerEmailPorId(id));
    }

    //* POST /api/emails — registra un nuevo email y retorna el EmailDTO creado
    //! Responde con HTTP 201 Created
    @PostMapping
    public ResponseEntity<EmailDTO> agregar(@Valid @RequestBody AgregarEmail nuevoEmail) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(emailService.agregarEmail(nuevoEmail));
    }

    //* PUT /api/emails/{id} — actualiza la dirección de email y retorna el EmailDTO
    @PutMapping("/{id}")
    public ResponseEntity<EmailDTO> actualizar(@PathVariable int id,
                                            @Valid @RequestBody ActualizarEmail actEmail) {
        return ResponseEntity.ok(emailService.actualizarEmail(id, actEmail));
    }

    //* DELETE /api/emails/{id} — elimina un email por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        return ResponseEntity.ok(emailService.eliminarEmail(id));
    }

}
