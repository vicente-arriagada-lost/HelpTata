package com.PreguntasRespuestas.ms.models.dto;

public record PreguntaDTO(
    int id_pregunta,
    String enunciado_pregunta,
    int id_cuestionario
) {}
