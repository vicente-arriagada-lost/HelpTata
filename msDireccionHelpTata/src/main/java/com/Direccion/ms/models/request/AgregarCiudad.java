package com.Direccion.ms.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

//* DTO para crear una nueva ciudad — solo contiene los campos que el cliente debe enviar
//? @NotBlank valida que el String no sea null ni vacío
//? @Positive valida que el número sea mayor que cero
//! id_ciudad se omite: lo genera automáticamente la base de datos
@Data
public class AgregarCiudad {

    //* Nombre de la ciudad (ej: "Santiago")
    @NotBlank
    private String nombre_ciudad;

    //* ID de la región a la que pertenece esta ciudad — el servicio valida que exista en BD
    @Positive
    private int id_region;

}
