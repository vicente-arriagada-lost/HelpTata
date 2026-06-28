package com.Usuario.ms;

// =============================================================
// TESTS UNITARIOS — UsuarioServiceTest.java
// =============================================================
// Pruebas de la lógica de negocio de UsuarioService usando Mockito.
// No se necesita base de datos: los repositorios y clientes
// externos son reemplazados por mocks que simulan su comportamiento.
//
// Patrón AAA usado en cada test:
//   Arrange → configurar los mocks con when(...)
//   Act     → llamar al método del servicio
//   Assert  → verificar resultado con assertEquals / assertThrows
//
// Ejecutar con: ./mvnw test
// =============================================================

import com.Usuario.ms.clients.DireccionClient;
import com.Usuario.ms.clients.LogClient;
import com.Usuario.ms.config.CustomUserDetailsService;
import com.Usuario.ms.config.JwtUtil;
import com.Usuario.ms.models.dto.UsuarioDTO;
import com.Usuario.ms.models.entities.Usuario;
import com.Usuario.ms.models.request.AgregarUsuario;
import com.Usuario.ms.repositories.UsuarioRepository;
import com.Usuario.ms.services.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

//* @ExtendWith habilita la integración de Mockito con JUnit 5
@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService — pruebas unitarias")
class UsuarioServiceTest {

    //* Mocks: simulan los colaboradores del servicio sin tocar BD ni red
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private DireccionClient direccionClient;
    @Mock private LogClient logClient;
    @Mock private JwtUtil jwtUtil;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private CustomUserDetailsService customUserDetailsService;

    //* @InjectMocks crea una instancia real del servicio e inyecta los mocks anteriores
    @InjectMocks
    private UsuarioService usuarioService;

    //* Entidad de usuario reutilizada en los tests para evitar repetición
    private Usuario usuarioBase;

    @BeforeEach
    void setUp() {
        //* Prepara un usuario completo con todos los campos obligatorios
        usuarioBase = new Usuario();
        usuarioBase.setId_usuario(1);
        usuarioBase.setRun_usuario("12345678");
        usuarioBase.setDvrun_usuario("9");
        usuarioBase.setPnombre_usuario("Juan");
        usuarioBase.setSnombre_usuario("Carlos");
        usuarioBase.setPapellido_usuario("Pérez");
        usuarioBase.setSapellido_usuario("González");
        usuarioBase.setFecha_nac_usuario(LocalDate.of(1990, 5, 15));
        usuarioBase.setTelefono_usuario("+56912345678");
        //* La contraseña ya viene hasheada — representa el estado en BD
        usuarioBase.setPassword_usuario("$2a$10$hashedpassword");
        usuarioBase.setFecha_reg_usuario(LocalDate.now());
        usuarioBase.setId_rol(2);
    }

    // ── obtenerTodosLosUsuarios ────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerTodosLosUsuarios: retorna lista de DTOs cuando hay usuarios")
    void obtenerTodosLosUsuarios_retornaLista() {
        //* Arrange: el repositorio devuelve un usuario
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioBase));

        //* Act: llamar al método del servicio
        List<UsuarioDTO> resultado = usuarioService.obtenerTodosLosUsuarios();

        //* Assert: verifica que la lista tiene un elemento con los datos correctos
        assertEquals(1, resultado.size());
        assertEquals(1, resultado.get(0).id_usuario());
        assertEquals("Juan", resultado.get(0).pnombre_usuario());
        //* Verifica que el repositorio fue consultado exactamente una vez
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("obtenerTodosLosUsuarios: retorna lista vacía cuando no hay usuarios")
    void obtenerTodosLosUsuarios_listaVacia() {
        //* Arrange: el repositorio devuelve lista vacía (BD sin registros)
        when(usuarioRepository.findAll()).thenReturn(List.of());

        List<UsuarioDTO> resultado = usuarioService.obtenerTodosLosUsuarios();

        assertTrue(resultado.isEmpty());
    }

    // ── obtenerUsuarioPorId ────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerUsuarioPorId: retorna DTO cuando el ID existe")
    void obtenerUsuarioPorId_existente() {
        //* Arrange: el repositorio encuentra el usuario con ID 1
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioBase));

        //* Act
        UsuarioDTO resultado = usuarioService.obtenerUsuarioPorId(1);

        //* Assert: los datos del DTO coinciden con los de la entidad
        assertNotNull(resultado);
        assertEquals(1, resultado.id_usuario());
        assertEquals("Pérez", resultado.papellido_usuario());
    }

    @Test
    @DisplayName("obtenerUsuarioPorId: lanza 404 cuando el ID no existe")
    void obtenerUsuarioPorId_noExiste_lanza404() {
        //* Arrange: el repositorio no encuentra nada para ID 99
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        //! El servicio debe lanzar ResponseStatusException (HTTP 404), no retornar null
        assertThrows(ResponseStatusException.class,
                () -> usuarioService.obtenerUsuarioPorId(99));
    }

    // ── agregarUsuario ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("agregarUsuario: hashea la contraseña con BCrypt antes de guardar")
    void agregarUsuario_hashea_password() {
        //* Arrange: request con contraseña en texto plano
        AgregarUsuario req = new AgregarUsuario();
        req.setRun_usuario("12345678");
        req.setDvrun_usuario("9");
        req.setPnombre_usuario("Juan");
        req.setPapellido_usuario("Pérez");
        req.setFecha_nac_usuario(LocalDate.of(1990, 5, 15));
        req.setTelefono_usuario("+56912345678");
        req.setPassword_usuario("ClaveTextoPlano1!");

        //* El encoder transforma la clave plana en un hash BCrypt
        when(passwordEncoder.encode("ClaveTextoPlano1!")).thenReturn("$2a$10$hashedpassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioBase);

        //* Act
        usuarioService.agregarUsuario(req);

        //* Assert: verifica que la contraseña se hasheó — nunca se guarda texto plano
        verify(passwordEncoder, times(1)).encode("ClaveTextoPlano1!");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("agregarUsuario: asigna id_rol=2 (USER) por defecto")
    void agregarUsuario_asignaRolUser() {
        //* Arrange
        AgregarUsuario req = new AgregarUsuario();
        req.setRun_usuario("12345678");
        req.setDvrun_usuario("9");
        req.setPnombre_usuario("Juan");
        req.setPapellido_usuario("Pérez");
        req.setFecha_nac_usuario(LocalDate.of(1990, 5, 15));
        req.setTelefono_usuario("+56912345678");
        req.setPassword_usuario("Clave1!");

        when(passwordEncoder.encode(any())).thenReturn("$2a$10$hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            //! id_rol=2 es el rol USER — no debe dejarse null ni asignarse otro valor por defecto
            assertEquals(2, u.getId_rol());
            return usuarioBase;
        });

        usuarioService.agregarUsuario(req);
    }

    // ── eliminarUsuario ────────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminarUsuario: elimina correctamente cuando el usuario existe")
    void eliminarUsuario_existente() {
        //* Arrange: el usuario con ID 1 existe en BD
        when(usuarioRepository.existsById(1)).thenReturn(true);
        //* doNothing evita que el mock intente borrar algo real
        doNothing().when(usuarioRepository).deleteById(1);

        //* Act
        String resultado = usuarioService.eliminarUsuario(1);

        //* Assert
        assertEquals("Usuario eliminado correctamente.", resultado);
        verify(usuarioRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("eliminarUsuario: lanza 404 cuando el usuario no existe")
    void eliminarUsuario_noExiste_lanza404() {
        //* Arrange: el usuario con ID 99 no existe
        when(usuarioRepository.existsById(99)).thenReturn(false);

        //! Si el usuario no existe, nunca debe llamarse deleteById
        assertThrows(ResponseStatusException.class,
                () -> usuarioService.eliminarUsuario(99));
        verify(usuarioRepository, never()).deleteById(anyInt());
    }

    // ── login ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("login: lanza excepción cuando las credenciales son incorrectas")
    void login_credencialesIncorrectas_lanzaExcepcion() {
        com.Usuario.ms.models.request.LoginRequest req =
                new com.Usuario.ms.models.request.LoginRequest("malo@test.com", "wrongpass");

        //* AuthenticationManager lanza BadCredentialsException con credenciales malas
        //* Esto simula el rechazo de Spring Security sin necesitar una BD real
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciales inválidas"));

        assertThrows(ResponseStatusException.class, () -> usuarioService.login(req));
    }
}
