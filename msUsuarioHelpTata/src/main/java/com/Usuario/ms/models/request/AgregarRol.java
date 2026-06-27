package com.Usuario.ms.models.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

//* DTO para crear un nuevo rol
//! id_rol se omite: lo genera automáticamente la base de datos
@Data
public class AgregarRol {

    //* Tipo de rol a registrar (ej: "ADMIN", "USER", "MODERATOR")
    @NotBlank
    private String tipo_rol;

}
