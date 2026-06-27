package com.Direccion.ms.models.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

//* DTO para crear un nuevo país — solo contiene los campos que el cliente debe enviar
//? @NotBlank valida que el String no sea null ni vacío (ni solo espacios)
//! id_pais se omite: lo genera automáticamente la base de datos
@Data
public class AgregarPais {

    //* Nombre del país (ej: "Chile")
    @NotBlank
    private String nombre_pais;

}
