package com.Logs.ms.models.dto;

import java.time.LocalDateTime;

// DTO de respuesta para Log
public record LogDTO(
        int id_log,
        String tipo_log,
        String servicio_origen,
        String mensaje_log,
        LocalDateTime fecha_hora_log,
        Integer id_usuario,
        String ip_log,
        String detalle_log
) {}
