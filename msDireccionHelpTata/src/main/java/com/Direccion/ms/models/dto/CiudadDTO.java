package com.Direccion.ms.models.dto;

// DTO de respuesta para Ciudad
// En vez de incluir el objeto Region completo, solo se incluye su ID
public record CiudadDTO(int id_ciudad, String nombre_ciudad, int id_region) {}
