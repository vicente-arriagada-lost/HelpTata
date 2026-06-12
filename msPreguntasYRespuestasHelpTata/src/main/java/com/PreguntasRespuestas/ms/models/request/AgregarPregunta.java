package com.PreguntasRespuestas.ms.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AgregarPregunta {

    @NotBlank
    private String enunciado_pregunta;

    @Positive
    private int id_cuestionario;
}
