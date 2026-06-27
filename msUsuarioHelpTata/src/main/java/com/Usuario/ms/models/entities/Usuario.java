package com.Usuario.ms.models.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

//* Entidad JPA que representa la tabla "usuarios" en la base de datos
//? @Data de Lombok genera automáticamente getters, setters, equals, hashCode y toString
@Entity
@Data
@Table(name = "usuarios")
public class Usuario {

    //* Clave primaria autoincremental
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_usuario;

    //* RUN sin dígito verificador (ej: "12345678")
    @Column(nullable = false)
    private String run_usuario;

    //* Dígito verificador del RUN (ej: "9" o "K")
    @Column(nullable = false)
    private String dvrun_usuario;

    //* Primer nombre del usuario
    @Column(nullable = false)
    private String pnombre_usuario;

    //* Segundo nombre — nullable porque no todos tienen segundo nombre
    @Column(nullable = true)
    private String snombre_usuario;

    //* Primer apellido (apellido paterno)
    @Column(nullable = false)
    private String papellido_usuario;

    //* Segundo apellido (apellido materno) — nullable por compatibilidad
    @Column(nullable = true)
    private String sapellido_usuario;

    //* Fecha de nacimiento del usuario
    @Column(nullable = false)
    private LocalDate fecha_nac_usuario;

    //* Número de teléfono de contacto (incluye prefijo internacional, ej: +56912345678)
    @Column(nullable = false)
    private String telefono_usuario;

    //! La contraseña debe cifrarse con BCrypt antes de guardarse — pendiente implementar
    @Column(nullable = false)
    private String password_usuario;

    //* Fecha en que se registró el usuario — asignada automáticamente en el servicio
    @Column(nullable = false)
    private LocalDate fecha_reg_usuario;

    //* ID de la dirección del usuario — referencia al MS Direccion (sin JPA cross-MS)
    //* Para obtener el domicilio completo, el servicio llama a GET /api/direcciones/{id}
    @Column(nullable = true)
    private Integer id_direccion;

    //* ID del rol del usuario — referencia a la tabla Rol (nullable: valor por defecto "USER")
    @Column(nullable = true)
    private Integer id_rol;

}
