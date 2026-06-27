package com.Usuario.ms.config;

import com.Usuario.ms.models.entities.Email;
import com.Usuario.ms.models.entities.Rol;
import com.Usuario.ms.models.entities.Usuario;
import com.Usuario.ms.models.security.UsuarioPrincipal;
import com.Usuario.ms.repositories.EmailRepository;
import com.Usuario.ms.repositories.RolRepository;
import com.Usuario.ms.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// =============================================================
// CARGA DE USUARIO PARA SPRING SECURITY — CustomUserDetailsService.java
// =============================================================
// Spring Security llama a loadUserByUsername() en dos momentos:
//   1. Cuando el AuthenticationManager valida las credenciales del login.
//   2. Cuando el JwtFilter verifica que el usuario del token existe en la BD.
//
// En HelpTata el "username" es el email, que vive en la tabla Email separada
// (id_email == id_usuario por convención de diseño del proyecto).
// Este servicio hace el "puente" entre las tres tablas: Email, Usuario y Rol.
// =============================================================
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Paso 1: buscar el email en la tabla Email
        Email emailEntity = emailRepository.findByEmailAddress(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No encontramos una cuenta con el correo: " + email));

        // Paso 2: obtener el usuario usando la convención id_email == id_usuario
        Usuario usuario = usuarioRepository.findById(emailEntity.getId_email())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No encontramos una cuenta con el correo: " + email));

        // Paso 3: obtener el nombre del rol del usuario (por defecto "USER" si no tiene asignado)
        String rolNombre = "USER";
        if (usuario.getId_rol() != null) {
            rolNombre = rolRepository.findById(usuario.getId_rol())
                    .map(Rol::getTipo_rol)
                    .orElse("USER");
        }

        return new UsuarioPrincipal(usuario, email, rolNombre);
    }
}
