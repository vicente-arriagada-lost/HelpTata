package com.Direccion.ms.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

//* DTO para crear una nueva región — solo contiene los campos que el cliente debe enviar
//? @NotBlank valida que el String no sea null ni vacío
//? @Positive valida que el número sea mayor que cero
//! id_region se omite: lo genera automáticamente la base de datos
@Data
public class AgregarRegion {

    //* Nombre de la región (ej: "Región Metropolitana")
    @NotBlank
    private String nombre_region;

    //* ID del país al que pertenece esta región — el servicio valida que exista en BD
    @Positive
    private int id_pais;

}
