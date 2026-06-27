package com.Usuario.ms.models.security;

import com.Usuario.ms.models.entities.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// =============================================================
// ADAPTADOR DE USUARIO PARA SPRING SECURITY — UsuarioPrincipal.java
// =============================================================
// Spring Security necesita que los usuarios implementen UserDetails.
// En vez de modificar la entidad Usuario (que es un modelo de BD),
// esta clase la "envuelve" y expone lo que Spring necesita.
//
// Lleva tres datos que la entidad Usuario no tiene directamente:
//   - email → viene de la tabla Email (id_email == id_usuario)
//   - rol   → nombre del rol (ej: "USER", "ADMIN")
// =============================================================
public class UsuarioPrincipal implements UserDetails {

    private final Usuario usuario;
    private final String email;
    private final String rol;

    public UsuarioPrincipal(Usuario usuario, String email, String rol) {
        this.usuario = usuario;
        this.email = email;
        this.rol = rol;
    }

    // ── Datos del negocio (usados en servicios y en el JWT) ──

    public int getIdUsuario()         { return usuario.getId_usuario(); }
    public String getPnombreUsuario() { return usuario.getPnombre_usuario(); }
    public String getPapellidoUsuario(){ return usuario.getPapellido_usuario(); }
    public String getRol()            { return rol; }

    // RUT completo con dígito verificador (ej: "12345678-9")
    public String getRut() {
        return usuario.getRun_usuario() + "-" + usuario.getDvrun_usuario();
    }

    // ── Contrato UserDetails (requerido por Spring Security) ──

    // El "username" en Spring Security es el email en HelpTata
    @Override public String getUsername() { return email; }

    // Contraseña hasheada con BCrypt
    @Override public String getPassword() { return usuario.getPassword_usuario(); }

    // Rol del usuario como autoridad de Spring Security (requiere prefijo "ROLE_")
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol));
    }

    // HelpTata no maneja bloqueos ni expiración de cuentas por ahora
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }
}
