package com.Direccion.ms.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

//* DTO para actualizar una dirección existente
//! id_direccion se omite: viaja en el path de la URL (PUT /api/direcciones/{id})
@Data
public class ActualizarDireccion {

    //* Nueva calle
    @NotBlank
    private String calle;

    //* Nuevo número de la propiedad
    @NotBlank
    private String numero;

    //* ID de la comuna a la que pertenecerá esta dirección tras la actualización
    @Positive
    private int id_comuna;

}
