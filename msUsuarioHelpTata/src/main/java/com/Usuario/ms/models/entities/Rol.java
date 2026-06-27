package com.Usuario.ms.models.entities;

import jakarta.persistence.*;
import lombok.Data;

//* Entidad JPA que representa la tabla "rol" en la base de datos
@Entity
@Data
@Table(name = "rol")
public class Rol {

    //* Clave primaria autoincremental
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_rol;

    //* Tipo de rol asignado (ej: "ADMIN", "USER", "MODERATOR")
    @Column(nullable = false)
    private String tipo_rol;

}
