package com.Direccion.ms.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

//* DTO para actualizar una ciudad existente
//! id_ciudad se omite: viaja en el path de la URL (PUT /api/ciudades/{id})
@Data
public class ActualizarCiudad {

    //* Nuevo nombre de la ciudad
    @NotBlank
    private String nombre_ciudad;

    //* ID de la región a la que pertenecerá esta ciudad tras la actualización
    @Positive
    private int id_region;

}
