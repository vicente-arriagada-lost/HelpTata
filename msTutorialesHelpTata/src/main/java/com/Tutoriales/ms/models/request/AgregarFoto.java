package com.Tutoriales.ms.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

//* DTO para agregar una foto a un tutorial — solo contiene los campos que el cliente debe enviar
//? @NotBlank valida que el String no sea null ni vacío
//? @Positive valida que el número sea mayor que cero
//! id_foto se omite: lo genera automáticamente la base de datos
@Data
public class AgregarFoto {

    //* URL externa o contenido en base64 de la imagen
    @NotBlank
    private String foto;

    //* ID del tutorial al que pertenece esta foto — el servicio valida que exista en BD
    @Positive
    private int id_tutor;

}
