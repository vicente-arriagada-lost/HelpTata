package com.Direccion.ms.models.entities;

import jakarta.persistence.*;
import lombok.Data;

//* Entidad JPA que representa la tabla "comunas" en la base de datos
//* Una comuna pertenece a una ciudad — relación N:1 (muchas comunas, una ciudad)
//? @Data de Lombok genera automáticamente getters, setters, equals, hashCode y toString
@Entity
@Data
@Table(name = "comunas")
public class Comuna {

    //* Clave primaria autoincremental
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_comuna;

    //* Nombre de la comuna (ej: "Providencia", "Las Condes")
    @Column(nullable = false)
    private String nombre_comuna;

    //* Relación N:1 con Ciudad — JPA genera la columna "id_ciudad" como FK en la tabla "comunas"
    //? @ManyToOne indica que muchas comunas pueden pertenecer a la misma ciudad
    //? @JoinColumn define el nombre de la columna FK en la BD
    @ManyToOne
    @JoinColumn(name = "id_ciudad", nullable = false)
    private Ciudad ciudad;

}
