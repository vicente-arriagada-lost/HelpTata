package com.Evaluaciones.ms.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

//* DTO para crear una nueva evaluación — solo contiene los campos que el cliente debe enviar
//? @NotBlank valida que el String no sea null ni vacío
//? @Positive valida que el número sea mayor que cero
//! id_eva se omite: lo genera automáticamente la base de datos
@Data
public class AgregarEvaluacion {

    //* Nombre de la evaluación (ej: "Prueba Final de Programación Básica")
    @NotBlank
    private String nombre_eva;

    //* Tipo de evaluación (ej: "QUIZ", "EXAMEN", "PRACTICA", "AUTOEVALUACION")
    @NotBlank
    private String tipo_eva;

    //* Nivel de dificultad (ej: "BASICO", "INTERMEDIO", "AVANZADO")
    @NotBlank
    private String nivel_eva;

    //* Cantidad de preguntas en el banco — debe ser al menos 1
    @Positive
    private int banco_preg;

    //* ID del tutorial al que pertenece esta evaluación (referencia al MS Tutoriales)
    @Positive
    private int id_tutor;

}
