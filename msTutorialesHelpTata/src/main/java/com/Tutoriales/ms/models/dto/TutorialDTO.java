package com.Tutoriales.ms.models.dto;

// DTO de respuesta para Tutorial
public record TutorialDTO(
        int id_tutor,
        String nombre_tuto,
        String cat_tuto,
        String nivel_tuto,
        String tutorial,
        Integer tiempo_tutorial,
        String descripcion_tuto
) {}
