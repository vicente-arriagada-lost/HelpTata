package com.PreguntasRespuestas.ms.models.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class SubmitirCuestionario {

    @Positive
    private int id_usuario;

    @NotEmpty
    private List<RespuestaItem> respuestas;

    @Data
    public static class RespuestaItem {
        @Positive
        private int id_pregunta;

        @Positive
        private int id_alternativa_seleccionada;
    }
}
