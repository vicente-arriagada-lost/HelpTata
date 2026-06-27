package com.Tutoriales.ms.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

//* DTO para crear un nuevo tutorial — solo contiene los campos que el cliente debe enviar
//? @NotBlank valida que el String no sea null ni vacío
//? @Positive valida que el número sea mayor que cero
//! id_tutor se omite: lo genera automáticamente la base de datos
@Data
public class AgregarTutorial {

    //* Nombre del tutorial
    @NotBlank
    private String nombre_tuto;

    //* Categoría del tutorial (ej: "Programación", "Diseño")
    @NotBlank
    private String cat_tuto;

    //* Nivel de dificultad — opcional (ej: "BASICO", "INTERMEDIO", "AVANZADO")
    private String nivel_tuto;

    //* URL o enlace al contenido del tutorial — opcional
    private String tutorial;

    //* Duración en minutos — opcional, debe ser positivo si se envía
    @Positive
    private Integer tiempo_tutorial;

    //* Descripción del tutorial — opcional
    private String descripcion_tuto;

}
