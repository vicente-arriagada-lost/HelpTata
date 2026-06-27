package com.Direccion.ms.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

//* DTO para actualizar una comuna existente
//! id_comuna se omite: viaja en el path de la URL (PUT /api/comunas/{id})
@Data
public class ActualizarComuna {

    //* Nuevo nombre de la comuna
    @NotBlank
    private String nombre_comuna;

    //* ID de la ciudad a la que pertenecerá esta comuna tras la actualización
    @Positive
    private int id_ciudad;

}
