package com.Tutoriales.ms.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

//* DTO para actualizar un tutorial existente
//? El id_tutor viene desde el path de la URL, no del body
@Data
public class ActualizarTutorial {

    //* Nombre del tutorial
    @NotBlank
    private String nombre_tuto;

    //* Categoría del tutorial
    @NotBlank
    private String cat_tuto;

    //* Nivel de dificultad — opcional
    private String nivel_tuto;

    //* URL o enlace al contenido — opcional
    private String tutorial;

    //* Duración en minutos — opcional, debe ser positivo si se envía
    @Positive
    private Integer tiempo_tutorial;

    //* Descripción — opcional
    private String descripcion_tuto;

}
