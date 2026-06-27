package com.Tutoriales.ms.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

//* DTO para actualizar una foto existente
//! id_foto se omite: viaja en el path de la URL (PUT /api/fotos/{id})
@Data
public class ActualizarFoto {

    //* Nueva URL o contenido en base64 de la imagen
    @NotBlank
    private String foto;

    //* ID del tutorial al que pertenecerá esta foto tras la actualización
    @Positive
    private int id_tutor;

}
