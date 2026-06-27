package com.Direccion.ms.models.entities;

import jakarta.persistence.*;
import lombok.Data;

//* Entidad JPA que representa la tabla "ciudades" en la base de datos
//* Una ciudad pertenece a una región — relación N:1 (muchas ciudades, una región)
//? @Data de Lombok genera automáticamente getters, setters, equals, hashCode y toString
@Entity
@Data
@Table(name = "ciudades")
public class Ciudad {

    //* Clave primaria autoincremental
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_ciudad;

    //* Nombre de la ciudad (ej: "Santiago", "Valparaíso")
    @Column(nullable = false)
    private String nombre_ciudad;

    //* Relación N:1 con Region — JPA genera la columna "id_region" como FK en la tabla "ciudades"
    //? @ManyToOne indica que muchas ciudades pueden pertenecer a la misma región
    //? @JoinColumn define el nombre de la columna FK en la BD
    @ManyToOne
    @JoinColumn(name = "id_region", nullable = false)
    private Region region;

}
