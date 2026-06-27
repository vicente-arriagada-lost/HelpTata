package com.Tutoriales.ms.models.entities;

import jakarta.persistence.*;
import lombok.Data;

//* Entidad JPA que representa la tabla "configuracion" en la base de datos.
//* Almacena pares clave-valor para configuraciones globales del sistema,
//* como la URL del video tutorial del cuestionario.
@Entity
@Data
@Table(name = "configuracion")
public class Configuracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_config")
    private int idConfig;

    @Column(name = "clave_config", nullable = false, unique = true)
    private String claveConfig;

    @Column(name = "valor_config", nullable = false, length = 500)
    private String valorConfig;
}
