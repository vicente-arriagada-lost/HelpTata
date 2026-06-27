package com.Tutoriales.ms.models.entities;

import jakarta.persistence.*;
import lombok.Data;

//* Entidad JPA que representa la tabla "tutoriales" en la base de datos
//? @Data de Lombok genera automáticamente getters, setters, equals, hashCode y toString
@Entity
@Data
@Table(name = "tutoriales")
public class Tutorial {

    //* Clave primaria autoincremental
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_tutor;

    //* Nombre del tutorial
    @Column(nullable = false)
    private String nombre_tuto;

    //* Categoría del tutorial (ej: "Programación", "Diseño", "Matemáticas")
    @Column(nullable = false)
    private String cat_tuto;

    //* Nivel de dificultad — nullable (ej: "BASICO", "INTERMEDIO", "AVANZADO")
    @Column(nullable = true)
    private String nivel_tuto;

    //* URL o enlace al contenido del tutorial — nullable
    @Column(nullable = true)
    private String tutorial;

    //* Duración estimada en minutos — nullable
    @Column(nullable = true)
    private Integer tiempo_tutorial;

    //* Descripción del contenido del tutorial — nullable
    @Column(nullable = true, length = 1000)
    private String descripcion_tuto;

}
