package com.Usuario.ms.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.Usuario.ms.clients.DireccionClient;
import com.Usuario.ms.clients.LogClient;
import com.Usuario.ms.config.CustomUserDetailsService;
import com.Usuario.ms.config.JwtUtil;
import com.Usuario.ms.models.dto.LoginResponse;
import com.Usuario.ms.models.dto.UsuarioDTO;
import com.Usuario.ms.models.entities.Usuario;
import com.Usuario.ms.models.request.ActualizarUsuario;
import com.Usuario.ms.models.request.AgregarUsuario;
import com.Usuario.ms.models.request.LoginRequest;
import com.Usuario.ms.models.security.UsuarioPrincipal;
import com.Usuario.ms.repositories.UsuarioRepository;

//* Servicio que encapsula toda la lógica de negocio relacionada a usuarios
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DireccionClient direccionClient;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //* AuthenticationManager delega la verificación de credenciales a Spring Security
    //* (busca el usuario con CustomUserDetailsService y compara el hash BCrypt)
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private LogClient logClient;

    //* Convierte una entidad Usuario a su DTO de respuesta (sin password)
    private UsuarioDTO toDTO(Usuario u) {
        return new UsuarioDTO(
                u.getId_usuario(),
                u.getRun_usuario(),
                u.getDvrun_usuario(),
                u.getPnombre_usuario(),
                u.getSnombre_usuario(),
                u.getPapellido_usuario(),
                u.getSapellido_usuario(),
                u.getFecha_nac_usuario(),
                u.getTelefono_usuario(),
                u.getFecha_reg_usuario(),
                u.getId_direccion()
        );
    }

    //* Autentica un usuario por email + contraseña y retorna un JWT
    public LoginResponse login(LoginRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.email(), req.password())
            );
        } catch (AuthenticationException e) {
            logClient.registrar("WARNING",
                    "Login fallido: credenciales incorrectas para " + req.email(),
                    null, e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos.");
        }

        UsuarioPrincipal principal =
                (UsuarioPrincipal) customUserDetailsService.loadUserByUsername(req.email());

        String nombre = principal.getPnombreUsuario() + " " + principal.getPapellidoUsuario();
        String token = jwtUtil.generateToken(principal, principal.getRut(), principal.getRol(),
                principal.getIdUsuario(), nombre);

        logClient.registrar("AUTENTICACION",
                "Login exitoso: " + req.email(),
                principal.getIdUsuario(), null);

        return new LoginResponse(
                principal.getIdUsuario(),
                principal.getPnombreUsuario(),
                principal.getPapellidoUsuario(),
                req.email(),
                principal.getRol(),
                token
        );
    }

    //* Retorna la lista completa de usuarios como DTOs (sin passwords)
    public List<UsuarioDTO> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    //* Busca un usuario por su ID y retorna el DTO
    //! Lanza HTTP 404 si el ID no existe en la BD
    public UsuarioDTO obtenerUsuarioPorId(int id_usuario) {
        Usuario usuario = usuarioRepository.findById(id_usuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));
        return toDTO(usuario);
    }

    //* Crea un nuevo usuario — hashea la contraseña con BCrypt antes de guardar
    //! fecha_reg_usuario se asigna aquí con LocalDate.now() — no viene del request
    public UsuarioDTO agregarUsuario(AgregarUsuario nuevoUsuario) {
        if (nuevoUsuario.getId_direccion() != null) {
            direccionClient.validarDireccion(nuevoUsuario.getId_direccion());
        }
        Usuario usuario = new Usuario();
        usuario.setRun_usuario(nuevoUsuario.getRun_usuario());
        usuario.setDvrun_usuario(nuevoUsuario.getDvrun_usuario());
        usuario.setPnombre_usuario(nuevoUsuario.getPnombre_usuario());
        usuario.setSnombre_usuario(nuevoUsuario.getSnombre_usuario());
        usuario.setPapellido_usuario(nuevoUsuario.getPapellido_usuario());
        usuario.setSapellido_usuario(nuevoUsuario.getSapellido_usuario());
        usuario.setFecha_nac_usuario(nuevoUsuario.getFecha_nac_usuario());
        usuario.setTelefono_usuario(nuevoUsuario.getTelefono_usuario());
        // Hashear la contraseña con BCrypt antes de guardar — nunca texto plano
        usuario.setPassword_usuario(passwordEncoder.encode(nuevoUsuario.getPassword_usuario()));
        usuario.setId_direccion(nuevoUsuario.getId_direccion());
        // Rol por defecto: 2 = "USER" (definido en data.sql)
        usuario.setId_rol(2);
        usuario.setFecha_reg_usuario(LocalDate.now());
        UsuarioDTO resultado = toDTO(usuarioRepository.save(usuario));
        logClient.registrar("INFO",
                "Usuario creado: " + nuevoUsuario.getPnombre_usuario() + " " + nuevoUsuario.getPapellido_usuario()
                        + " (RUT: " + nuevoUsuario.getRun_usuario() + "-" + nuevoUsuario.getDvrun_usuario() + ")",
                resultado.id_usuario(), null);
        return resultado;
    }

    //* Elimina un usuario por su ID
    //! Lanza HTTP 404 si el ID no existe
    public String eliminarUsuario(int id_usuario) {
        if (usuarioRepository.existsById(id_usuario)) {
            usuarioRepository.deleteById(id_usuario);
            logClient.registrar("INFO",
                    "Usuario eliminado: id=" + id_usuario,
                    id_usuario, null);
            return "Usuario eliminado correctamente.";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado.");
        }
    }

    //* Actualiza los datos de un usuario existente
    public UsuarioDTO actualizarUsuario(int id_usuario, ActualizarUsuario actUsuario) {
        Usuario usuario = usuarioRepository.findById(id_usuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));
        usuario.setRun_usuario(actUsuario.getRun_usuario());
        usuario.setDvrun_usuario(actUsuario.getDvrun_usuario());
        usuario.setPnombre_usuario(actUsuario.getPnombre_usuario());
        usuario.setSnombre_usuario(actUsuario.getSnombre_usuario());
        usuario.setPapellido_usuario(actUsuario.getPapellido_usuario());
        usuario.setSapellido_usuario(actUsuario.getSapellido_usuario());
        usuario.setFecha_nac_usuario(actUsuario.getFecha_nac_usuario());
        usuario.setTelefono_usuario(actUsuario.getTelefono_usuario());
        // Si se envía nueva contraseña en texto plano, se hashea; si es null/vacía, se conserva la actual
        String nuevaPassword = actUsuario.getPassword_usuario();
        if (nuevaPassword != null && !nuevaPassword.isBlank()) {
            if (!nuevaPassword.startsWith("$2a$")) {
                usuario.setPassword_usuario(passwordEncoder.encode(nuevaPassword));
            } else {
                usuario.setPassword_usuario(nuevaPassword);
            }
        }
        if (actUsuario.getId_direccion() != null) {
            direccionClient.validarDireccion(actUsuario.getId_direccion());
        }
        usuario.setId_direccion(actUsuario.getId_direccion());
        UsuarioDTO resultado = toDTO(usuarioRepository.save(usuario));
        logClient.registrar("INFO",
                "Usuario actualizado: id=" + id_usuario + " ("
                        + actUsuario.getPnombre_usuario() + " " + actUsuario.getPapellido_usuario() + ")",
                id_usuario, null);
        return resultado;
    }
}
