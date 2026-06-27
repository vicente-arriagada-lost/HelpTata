package com.Direccion.ms.models.entities;

import jakarta.persistence.*;
import lombok.Data;

//* Entidad JPA que representa la tabla "paises" en la base de datos
//? @Data de Lombok genera automáticamente getters, setters, equals, hashCode y toString
@Entity
@Data
@Table(name = "paises")
public class Pais {

    //* Clave primaria autoincremental
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_pais;

    //* Nombre del país (ej: "Chile", "Argentina")
    @Column(nullable = false)
    private String nombre_pais;

}
