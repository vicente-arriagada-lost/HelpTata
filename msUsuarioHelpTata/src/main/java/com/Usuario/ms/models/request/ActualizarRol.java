package com.Usuario.ms.models.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

//* DTO para actualizar un rol existente
//! id_rol se omite: viaja en el path de la URL (PUT /api/roles/{id})
@Data
public class ActualizarRol {

    //* Nuevo tipo de rol
    @NotBlank
    private String tipo_rol;

}
