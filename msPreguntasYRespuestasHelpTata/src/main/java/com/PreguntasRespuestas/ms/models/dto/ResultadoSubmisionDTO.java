package com.PreguntasRespuestas.ms.models.dto;

public record ResultadoSubmisionDTO(
    int id_resultado,
    int correctas,
    int incorrectas,
    double porcentaje
) {}
