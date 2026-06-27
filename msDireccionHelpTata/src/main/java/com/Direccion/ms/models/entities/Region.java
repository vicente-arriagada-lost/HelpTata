package com.Direccion.ms.models.entities;

import jakarta.persistence.*;
import lombok.Data;

//* Entidad JPA que representa la tabla "regiones" en la base de datos
//* Una región pertenece a un país — relación N:1 (muchas regiones, un país)
//? @Data de Lombok genera automáticamente getters, setters, equals, hashCode y toString
@Entity
@Data
@Table(name = "regiones")
public class Region {

    //* Clave primaria autoincremental
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_region;

    //* Nombre de la región (ej: "Región Metropolitana", "Región de Valparaíso")
    @Column(nullable = false)
    private String nombre_region;

    //* Relación N:1 con Pais — JPA genera la columna "id_pais" como FK en la tabla "regiones"
    //? @ManyToOne indica que muchas regiones pueden pertenecer al mismo país
    //? @JoinColumn define el nombre de la columna FK en la BD
    @ManyToOne
    @JoinColumn(name = "id_pais", nullable = false)
    private Pais pais;

}
