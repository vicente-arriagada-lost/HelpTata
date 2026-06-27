package com.Usuario.ms.config;

import com.Usuario.ms.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// =============================================================
// MIGRADOR DE CONTRASEÑAS — DataInitializer.java
// =============================================================
// Al arrancar la aplicación, detecta contraseñas en texto plano
// (las que no empiezan con "$2a$", prefijo de BCrypt) y las
// re-hashea automáticamente.
//
// Esto permite migrar datos existentes sin borrar la base de datos.
// Es idempotente: si las contraseñas ya están hasheadas, no hace nada.
// =============================================================
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        usuarioRepository.findAll().forEach(usuario -> {
            // BCrypt siempre empieza con "$2a$" — si no, la contraseña está en texto plano
            if (!usuario.getPassword_usuario().startsWith("$2a$")) {
                usuario.setPassword_usuario(
                        passwordEncoder.encode(usuario.getPassword_usuario())
                );
                usuarioRepository.save(usuario);
            }
        });
    }
}
