package com.Usuario.ms.models.dto;

//* DTO de respuesta del endpoint POST /api/usuarios/login.
//* El frontend recibe los datos del usuario y el token JWT.
//* El token debe guardarse en localStorage y enviarse en el
//* header "Authorization: Bearer <token>" de las peticiones siguientes.
public record LoginResponse(
        int    id_usuario,
        String pnombre_usuario,
        String papellido_usuario,
        String email,
        String rol,
        String token
) {}
