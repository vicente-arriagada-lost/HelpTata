package com.Direccion.ms.models.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

//* DTO para actualizar un país existente
//! id_pais se omite: viaja en el path de la URL (PUT /api/paises/{id})
@Data
public class ActualizarPais {

    //* Nuevo nombre del país
    @NotBlank
    private String nombre_pais;

}
