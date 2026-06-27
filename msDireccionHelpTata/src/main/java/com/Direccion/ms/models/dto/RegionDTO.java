package com.Direccion.ms.models.dto;

// DTO de respuesta para Region
// En vez de incluir el objeto Pais completo, solo se incluye su ID
public record RegionDTO(int id_region, String nombre_region, int id_pais) {}
