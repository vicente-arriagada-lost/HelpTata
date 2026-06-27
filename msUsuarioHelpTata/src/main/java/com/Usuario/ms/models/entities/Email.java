package com.Usuario.ms.models.entities;

import jakarta.persistence.*;
import lombok.Data;

//* Entidad JPA que representa la tabla "email" en la base de datos
@Entity
@Data
@Table(name = "email")
public class Email {

    //* Clave primaria autoincremental
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_email;

    //* Dirección de correo electrónico
    @Column(nullable = false)
    private String email;

}
