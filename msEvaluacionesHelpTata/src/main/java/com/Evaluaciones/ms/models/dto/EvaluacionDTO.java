package com.Evaluaciones.ms.models.dto;

// DTO de respuesta para Evaluacion
public record EvaluacionDTO(
        int id_eva,
        String nombre_eva,
        String tipo_eva,
        String nivel_eva,
        int banco_preg,
        int id_tutor
) {}
