package com.Usuario.ms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// =============================================================
// CONFIGURACIÓN DE SEGURIDAD — SecurityConfig.java
// =============================================================
// Define qué endpoints son públicos, cuáles requieren JWT y
// cómo Spring valida las credenciales (BCrypt + CustomUserDetailsService).
// =============================================================
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CORS gestionado aquí (tiene precedencia sobre WebConfig en peticiones protegidas)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // CSRF deshabilitado: APIs REST stateless no usan cookies de sesión
            .csrf(csrf -> csrf.disable())

            // Sin sesiones en servidor: cada petición se autentica por su propio JWT
            .sessionManagement(sess -> sess
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos: no requieren token
                .requestMatchers(HttpMethod.POST, "/api/usuarios/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                // El email se crea durante el registro (sin token aún), debe ser público
                .requestMatchers(HttpMethod.POST, "/api/emails").permitAll()
                // Swagger disponible sin autenticar en desarrollo
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**").permitAll()
                // Todo lo demás requiere un JWT válido
                .anyRequest().authenticated()
            )

            // Define cómo Spring verifica identidad: busca por email + compara con BCrypt
            .authenticationProvider(authenticationProvider())

            // El filtro JWT se ejecuta antes del filtro de autenticación estándar de Spring
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Proveedor de autenticación: usa CustomUserDetailsService para cargar el usuario
    // y BCryptPasswordEncoder para comparar la contraseña con el hash guardado en BD
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // AuthenticationManager expuesto como bean para que UsuarioService pueda inyectarlo
    // y llamar a authenticate() desde el flujo de login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // BCrypt: transforma "test123" en un hash seguro como "$2a$10$..."
    // Es unidireccional: no puedes recuperar la contraseña original
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configuración CORS: permite peticiones desde el frontend en localhost:5000
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
