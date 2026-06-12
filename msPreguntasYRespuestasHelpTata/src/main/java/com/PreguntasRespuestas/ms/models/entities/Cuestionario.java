package com.PreguntasRespuestas.ms.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Entity
@Data
@Table(name = "cuestionarios")
public class Cuestionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_cuestionario;

    @Column(nullable = false)
    @NotBlank
    private String titulo_cuestionario;

    @Column(length = 1000)
    private String descripcion_cuestionario;

    @Column(nullable = false)
    @Positive
    private int id_tutor;
}
