package com.Direccion.ms.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

//* DTO para crear una nueva dirección — solo contiene los campos que el cliente debe enviar
//* Este DTO es consumido principalmente por el MS Usuario al registrar el domicilio de un usuario
//? @NotBlank valida que el String no sea null ni vacío
//? @Positive valida que el número sea mayor que cero
//! id_direccion se omite: lo genera automáticamente la base de datos
@Data
public class AgregarDireccion {

    //* Nombre de la calle (ej: "Av. Providencia")
    @NotBlank
    private String calle;

    //* Número de la propiedad — String para soportar formatos como "123A" o "45 B"
    @NotBlank
    private String numero;

    //* ID de la comuna donde se ubica esta dirección — el servicio valida que exista en BD
    @Positive
    private int id_comuna;

}
