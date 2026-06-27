package com.Direccion.ms.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

//* DTO para crear una nueva comuna — solo contiene los campos que el cliente debe enviar
//? @NotBlank valida que el String no sea null ni vacío
//? @Positive valida que el número sea mayor que cero
//! id_comuna se omite: lo genera automáticamente la base de datos
@Data
public class AgregarComuna {

    //* Nombre de la comuna (ej: "Providencia")
    @NotBlank
    private String nombre_comuna;

    //* ID de la ciudad a la que pertenece esta comuna — el servicio valida que exista en BD
    @Positive
    private int id_ciudad;

}
