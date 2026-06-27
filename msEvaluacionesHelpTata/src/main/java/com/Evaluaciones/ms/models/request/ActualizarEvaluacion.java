package com.Evaluaciones.ms.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

//* DTO para actualizar una evaluación existente
//! id_eva se omite: viaja en el path de la URL (PUT /api/evaluaciones/{id})
@Data
public class ActualizarEvaluacion {

    //* Nuevo nombre de la evaluación
    @NotBlank
    private String nombre_eva;

    //* Nuevo tipo de evaluación
    @NotBlank
    private String tipo_eva;

    //* Nuevo nivel de dificultad
    @NotBlank
    private String nivel_eva;

    //* Nueva cantidad de preguntas en el banco
    @Positive
    private int banco_preg;

    //* ID del tutorial al que pertenecerá esta evaluación tras la actualización
    @Positive
    private int id_tutor;

}
