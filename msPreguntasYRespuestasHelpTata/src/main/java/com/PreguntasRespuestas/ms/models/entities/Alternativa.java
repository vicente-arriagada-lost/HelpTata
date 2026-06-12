package com.PreguntasRespuestas.ms.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Entity
@Data
@Table(name = "alternativas")
public class Alternativa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_alternativa;

    @Column(nullable = false)
    @NotBlank
    private String texto_alternativa;

    @Column(nullable = false)
    @NotNull
    private boolean es_correcta;

    @Column(nullable = false)
    @Positive
    private int id_pregunta;
}
