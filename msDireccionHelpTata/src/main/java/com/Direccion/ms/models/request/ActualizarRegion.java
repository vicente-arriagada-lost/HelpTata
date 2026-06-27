package com.Direccion.ms.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

//* DTO para actualizar una región existente
//! id_region se omite: viaja en el path de la URL (PUT /api/regiones/{id})
@Data
public class ActualizarRegion {

    //* Nuevo nombre de la región
    @NotBlank
    private String nombre_region;

    //* ID del país al que pertenecerá esta región tras la actualización
    @Positive
    private int id_pais;

}
