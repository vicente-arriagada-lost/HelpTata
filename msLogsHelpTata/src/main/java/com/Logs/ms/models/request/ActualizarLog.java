package com.Logs.ms.models.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

//* DTO para actualizar un log existente
//? El id_log viene desde el path de la URL, no del body
//? fecha_hora_log no se modifica — conserva el valor original del registro
@Data
public class ActualizarLog {

    //* Tipo del log: ERROR, WARNING, INFO, DEBUG, AUTENTICACION
    @NotBlank
    private String tipo_log;

    //* Microservicio o módulo que origina el log
    @NotBlank
    private String servicio_origen;

    //* Descripción del evento
    @NotBlank
    private String mensaje_log;

    //* ID del usuario relacionado — opcional
    private Integer id_usuario;

    //* IP de origen — opcional
    private String ip_log;

    //* Detalle extra — opcional
    private String detalle_log;

}
