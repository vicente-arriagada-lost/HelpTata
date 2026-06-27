package com.Direccion.ms.models.entities;

import jakarta.persistence.*;
import lombok.Data;

//* Entidad JPA que representa la tabla "direcciones" en la base de datos
//* Una dirección pertenece a una comuna — relación N:1 (muchas direcciones, una comuna)
//* Esta entidad es el punto de integración con el MS Usuario:
//*   el MS Usuario almacena solo el id_direccion y llama a este MS para obtener el detalle completo
//? @Data de Lombok genera automáticamente getters, setters, equals, hashCode y toString
@Entity
@Data
@Table(name = "direcciones")
public class Direccion {

    //* Clave primaria autoincremental — este ID es el que guarda el MS Usuario en su tabla
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_direccion;

    //* Nombre de la calle (ej: "Av. Providencia", "Los Aromos")
    @Column(nullable = false)
    private String calle;

    //* Número de la propiedad — se usa String para soportar formatos como "123A" o "45 B"
    @Column(nullable = false)
    private String numero;

    //* Relación N:1 con Comuna — JPA genera la columna "id_comuna" como FK en la tabla "direcciones"
    //? Al serializar a JSON, Hibernate incluye el objeto Comuna completo con su Ciudad, Región y País
    @ManyToOne
    @JoinColumn(name = "id_comuna", nullable = false)
    private Comuna comuna;

}
