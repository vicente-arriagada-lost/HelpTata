package com.Direccion.ms.models.dto;

// DTO de respuesta para Comuna
// En vez de incluir el objeto Ciudad completo, solo se incluye su ID
public record ComunaDTO(int id_comuna, String nombre_comuna, int id_ciudad) {}
