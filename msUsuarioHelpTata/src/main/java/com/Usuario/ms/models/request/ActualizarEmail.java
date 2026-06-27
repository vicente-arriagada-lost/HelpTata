package com.Usuario.ms.models.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

//* DTO para actualizar un email existente
//! id_email se omite: viaja en el path de la URL (PUT /api/emails/{id})
@Data
public class ActualizarEmail {

    //* Nueva dirección de correo electrónico
    @NotBlank
    @Email
    private String email;

}
