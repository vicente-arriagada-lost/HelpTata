package com.PreguntasRespuestas.ms.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "resultados_cuestionario")
public class ResultadoCuestionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_resultado;

    @Column(nullable = false)
    @Positive
    private int id_usuario;

    @Column(nullable = false)
    @Positive
    private int id_cuestionario;

    @Column(nullable = false)
    @Min(0)
    private int correctas;

    @Column(nullable = false)
    @Min(0)
    private int incorrectas;

    @Column(nullable = false)
    @Min(0)
    private double porcentaje;

    @Column(nullable = false)
    private LocalDateTime fecha_resultado;
}
