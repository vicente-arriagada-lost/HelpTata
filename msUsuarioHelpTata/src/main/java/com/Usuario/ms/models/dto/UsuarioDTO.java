package com.Usuario.ms.models.dto;

import java.time.LocalDate;

// DTO de respuesta para Usuario
// Se omite password_usuario para no exponerla en ninguna respuesta de la API
public record UsuarioDTO(
        int id_usuario,
        String run_usuario,
        String dvrun_usuario,
        String pnombre_usuario,
        String snombre_usuario,
        String papellido_usuario,
        String sapellido_usuario,
        LocalDate fecha_nac_usuario,
        String telefono_usuario,
        LocalDate fecha_reg_usuario,
        Integer id_direccion
) {}
