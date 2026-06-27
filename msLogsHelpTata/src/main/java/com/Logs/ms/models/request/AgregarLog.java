package com.Logs.ms.models.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

//* DTO para crear un nuevo log — solo contiene los campos que el cliente debe enviar
//? @NotBlank valida que el String no sea null ni vacío
//! id_log y fecha_hora_log se omiten: los genera automáticamente el servicio y la BD
@Data
public class AgregarLog {

    //* Tipo del log: ERROR, WARNING, INFO, DEBUG, AUTENTICACION
    @NotBlank
    private String tipo_log;

    //* Microservicio o módulo que origina el log
    @NotBlank
    private String servicio_origen;

    //* Descripción del evento
    @NotBlank
    private String mensaje_log;

    //* ID del usuario relacionado — opcional (null para procesos del sistema)
    private Integer id_usuario;

    //* IP de origen de la solicitud — opcional
    private String ip_log;

    //* Detalle extra: stack trace, payload, datos adicionales — opcional
    private String detalle_log;

}
