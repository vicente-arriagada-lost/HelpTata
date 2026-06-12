package com.PreguntasRespuestas.ms.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Entity
@Data
@Table(name = "respuestas_usuario")
public class RespuestaUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_respuesta;

    @Column(nullable = false)
    @Positive
    private int id_usuario;

    @Column(nullable = false)
    @Positive
    private int id_pregunta;

    @Column(nullable = false)
    @Positive
    private int id_alternativa_seleccionada;

    @Column(nullable = false)
    @Positive
    private int id_resultado;
}
