package com.PreguntasRespuestas.ms.models.dto;

public record CuestionarioDTO(
    int id_cuestionario,
    String titulo_cuestionario,
    String descripcion_cuestionario,
    int id_tutor
) {}
