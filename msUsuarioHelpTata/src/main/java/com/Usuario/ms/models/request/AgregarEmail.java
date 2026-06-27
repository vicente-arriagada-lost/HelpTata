package com.Usuario.ms.models.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

//* DTO para registrar un nuevo email
//! id_email se omite: lo genera automáticamente la base de datos
@Data
public class AgregarEmail {

    //* Dirección de correo — @Email valida que el formato sea válido (ej: user@mail.com)
    @NotBlank
    @Email
    private String email;

}
