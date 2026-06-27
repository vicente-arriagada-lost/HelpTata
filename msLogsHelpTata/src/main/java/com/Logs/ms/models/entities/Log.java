package com.Logs.ms.models.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

//* Entidad JPA que representa la tabla "logs" en la base de datos
//? @Data de Lombok genera automáticamente getters, setters, equals, hashCode y toString
@Entity
@Data
@Table(name = "logs")
public class Log {

    //* Clave primaria autoincremental
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_log;

    //* Tipo de log: ERROR, WARNING, INFO, DEBUG, AUTENTICACION
    @Column(nullable = false)
    private String tipo_log;

    //* Microservicio o módulo que generó el log (ej: "ms-usuario", "ms-auth")
    @Column(nullable = false)
    private String servicio_origen;

    //* Descripción del evento registrado
    @Column(nullable = false)
    private String mensaje_log;

    //* Fecha y hora exacta del evento — asignada automáticamente en el servicio
    @Column(nullable = false)
    private LocalDateTime fecha_hora_log;

    //* ID del usuario que provocó el evento — nullable porque puede ser un proceso del sistema
    @Column(nullable = true)
    private Integer id_usuario;

    //* Dirección IP desde donde se originó la solicitud — nullable para logs internos
    @Column(nullable = true)
    private String ip_log;

    //* Detalle adicional: stack trace, datos del request, información extra — nullable
    @Column(nullable = true, length = 2000)
    private String detalle_log;

}
