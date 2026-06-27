package com.Evaluaciones.ms.models.entities;

import jakarta.persistence.*;
import lombok.Data;

//* Entidad JPA que representa la tabla "evaluaciones" en la base de datos
//* Gestiona las pruebas asociadas a tutoriales: tipo, nivel y banco de preguntas
//* El campo id_tutor referencia al MS Tutoriales (sin JPA cross-MS)
//*   — para obtener los datos del tutorial se llama a GET /api/tutoriales/{id}
//? @Data de Lombok genera automáticamente getters, setters, equals, hashCode y toString
@Entity
@Data
@Table(name = "evaluaciones")
public class Evaluacion {

    //* Clave primaria autoincremental
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_eva;

    //* Nombre de la evaluación (ej: "Prueba Final de Programación Básica")
    @Column(nullable = false)
    private String nombre_eva;

    //* Tipo de evaluación (ej: "QUIZ", "EXAMEN", "PRACTICA", "AUTOEVALUACION")
    @Column(nullable = false)
    private String tipo_eva;

    //* Nivel de dificultad de la evaluación (ej: "BASICO", "INTERMEDIO", "AVANZADO")
    @Column(nullable = false)
    private String nivel_eva;

    //* Cantidad de preguntas disponibles en el banco de preguntas
    @Column(nullable = false)
    private int banco_preg;

    //* ID del tutorial al que pertenece esta evaluación — referencia al MS Tutoriales
    //! No se usa @ManyToOne porque el tutorial vive en un MS distinto (base de datos separada)
    //? Para validar que el tutorial existe, usar el EvaluacionesClient en el MS Tutoriales
    @Column(nullable = false)
    private int id_tutor;

}
