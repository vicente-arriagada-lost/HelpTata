package com.PreguntasRespuestas.ms.models.dto;

import java.time.LocalDateTime;

public record ResultadoCuestionarioDTO(
    int id_resultado,
    int id_usuario,
    int id_cuestionario,
    int correctas,
    int incorrectas,
    double porcentaje,
    LocalDateTime fecha_resultado
) {}
