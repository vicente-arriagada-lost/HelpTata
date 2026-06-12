package com.PreguntasRespuestas.ms.models.dto;

public record RespuestaUsuarioDTO(
    int id_respuesta,
    int id_usuario,
    int id_pregunta,
    int id_alternativa_seleccionada,
    int id_resultado
) {}
