package com.Tutoriales.ms.models.entities;

import jakarta.persistence.*;
import lombok.Data;

//* Entidad JPA que representa la tabla "fotos" en la base de datos
//* Una foto pertenece a un tutorial — relación N:1 (muchas fotos, un tutorial)
//* El campo "foto" almacena la URL o el contenido en base64 de la imagen
//? @Data de Lombok genera automáticamente getters, setters, equals, hashCode y toString
@Entity
@Data
@Table(name = "fotos")
public class Foto {

    //* Clave primaria autoincremental
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_foto;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String foto;

    //* Relación N:1 con Tutorial — JPA genera la columna "id_tutor" como FK en la tabla "fotos"
    //? @ManyToOne indica que muchas fotos pueden pertenecer al mismo tutorial
    //? @JoinColumn define el nombre de la columna FK en la BD
    @ManyToOne
    @JoinColumn(name = "id_tutor", nullable = false)
    private Tutorial tutorial;

}
