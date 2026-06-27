package com.Usuario.ms.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

//* DTO para actualizar un usuario existente
//! id_usuario se omite: viaja en el path de la URL (PUT /api/usuarios/{id})
//! fecha_reg_usuario se omite: la fecha de registro no se modifica nunca
@Data
public class ActualizarUsuario {

    //* RUN sin dígito verificador
    @NotBlank
    private String run_usuario;

    //* Dígito verificador del RUN
    @NotBlank
    private String dvrun_usuario;

    //* Primer nombre
    @NotBlank
    private String pnombre_usuario;

    //* Segundo nombre — opcional
    private String snombre_usuario;

    //* Primer apellido
    @NotBlank
    private String papellido_usuario;

    //* Segundo apellido — opcional
    private String sapellido_usuario;

    //* Fecha de nacimiento
    @NotNull
    private LocalDate fecha_nac_usuario;

    //* Teléfono — incluye prefijo internacional (ej: +56912345678)
    @NotBlank
    private String telefono_usuario;

    //* Nueva contraseña en texto plano — opcional: si es null o vacía, se mantiene la actual
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    private String password_usuario;

    //* ID de la dirección — opcional, se valida contra el MS Direccion si se envía
    private Integer id_direccion;

}
