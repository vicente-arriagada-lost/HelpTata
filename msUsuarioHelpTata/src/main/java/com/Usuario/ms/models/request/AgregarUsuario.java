package com.Usuario.ms.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

//* DTO para crear un nuevo usuario — solo contiene los campos que el cliente debe enviar
//? @NotBlank valida que el String no sea null ni vacío
//? @NotNull valida que el valor no sea null (para tipos que no son String)
//? @Positive valida que el número sea mayor que cero
@Data
public class AgregarUsuario {

    //* RUN sin dígito verificador
    @NotBlank
    private String run_usuario;

    //* Dígito verificador del RUN
    @NotBlank
    private String dvrun_usuario;

    //* Primer nombre
    @NotBlank
    private String pnombre_usuario;

    //* Segundo nombre — opcional, sin validación obligatoria
    private String snombre_usuario;

    //* Primer apellido
    @NotBlank
    private String papellido_usuario;

    //* Segundo apellido — opcional
    private String sapellido_usuario;

    //* Fecha de nacimiento — @NotNull porque LocalDate no es String
    @NotNull
    private LocalDate fecha_nac_usuario;

    //* Teléfono — incluye prefijo internacional (ej: +56912345678)
    @NotBlank
    private String telefono_usuario;

    //* Contraseña en texto plano — el servicio debe cifrarla antes de guardar
    @NotBlank
    private String password_usuario;

    //* ID de la dirección del usuario — opcional al registrar, asignable después
    //* El servicio valida que exista en el MS Direccion antes de guardar
    private Integer id_direccion;

    //! fecha_reg_usuario se omite: se asigna con LocalDate.now() en el servicio
    //! id_usuario se omite: lo genera automáticamente la base de datos
}
