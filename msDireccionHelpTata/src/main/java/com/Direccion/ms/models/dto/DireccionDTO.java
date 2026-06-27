package com.Direccion.ms.models.dto;

// DTO de respuesta para Direccion
// La entidad tiene un objeto Comuna completo (que a su vez tiene Ciudad -> Region -> Pais).
// Con este DTO solo se expone el id_comuna, evitando el JSON anidado profundo.
public record DireccionDTO(int id_direccion, String calle, String numero, int id_comuna) {}
