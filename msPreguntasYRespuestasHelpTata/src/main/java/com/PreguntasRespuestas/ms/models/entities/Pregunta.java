package com.PreguntasRespuestas.ms.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Entity
@Data
@Table(name = "preguntas")
public class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_pregunta;

    @Column(nullable = false)
    @NotBlank
    private String enunciado_pregunta;

    @Column(nullable = false)
    @Positive
    private int id_cuestionario;
}
