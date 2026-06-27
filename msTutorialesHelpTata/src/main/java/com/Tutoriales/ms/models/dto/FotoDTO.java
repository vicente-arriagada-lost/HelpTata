package com.Tutoriales.ms.models.dto;

// DTO de respuesta para Foto
// La entidad tiene un objeto Tutorial completo anidado.
// Con este DTO solo se expone el id_tutor, evitando el objeto Tutorial en la respuesta.
public record FotoDTO(int id_foto, String foto, int id_tutor) {}
