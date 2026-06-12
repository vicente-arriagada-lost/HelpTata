package com.PreguntasRespuestas.ms.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ActualizarCuestionario {

    @NotBlank
    private String titulo_cuestionario;

    private String descripcion_cuestionario;

    @Positive
    private int id_tutor;
}
