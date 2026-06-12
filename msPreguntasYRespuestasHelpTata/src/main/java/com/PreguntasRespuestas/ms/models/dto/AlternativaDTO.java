package com.PreguntasRespuestas.ms.models.dto;

public record AlternativaDTO(
    int id_alternativa,
    String texto_alternativa,
    boolean es_correcta,
    int id_pregunta
) {}
