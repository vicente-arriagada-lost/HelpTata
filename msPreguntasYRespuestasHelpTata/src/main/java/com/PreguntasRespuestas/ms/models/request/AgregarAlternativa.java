package com.PreguntasRespuestas.ms.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AgregarAlternativa {

    @NotBlank
    private String texto_alternativa;

    @NotNull
    private Boolean es_correcta;

    @Positive
    private int id_pregunta;
}
