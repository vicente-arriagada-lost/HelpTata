package com.Usuario.ms.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// =============================================================
// FILTRO DE VALIDACIÓN JWT — JwtFilter.java
// =============================================================
// Se ejecuta UNA vez por petición HTTP (OncePerRequestFilter).
// Actúa como "guardia de entrada": lee el token del header
// Authorization, valida su firma y vencimiento, y si es correcto
// autentica la petición en el contexto de Spring Security.
//
// Si el token falta o es inválido, la petición continúa sin
// autenticar y Spring Security decidirá si la rechaza según
// las reglas definidas en SecurityConfig.
// =============================================================
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Paso 1: leer el header "Authorization: Bearer <token>"
        final String authHeader = request.getHeader("Authorization");

        // Sin header o sin prefijo Bearer → petición pública, continuar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Paso 2: extraer el token eliminando "Bearer " (7 caracteres)
        final String jwt = authHeader.substring(7);

        try {
            // Paso 3: extraer el email del token (es el subject del JWT)
            final String email = jwtUtil.extractUsername(jwt);

            // Paso 4: solo procesar si hay email y el usuario no está autenticado aún
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Paso 5: cargar el usuario completo desde la BD para confirmar que existe
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                // Paso 6: validar firma y que el token no esté vencido
                if (jwtUtil.isTokenValid(jwt, userDetails)) {

                    // Paso 7: crear el objeto de autenticación con los roles del usuario
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );
                    // Adjuntar metadata de la petición (IP, session ID) para auditoría
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Paso 8: registrar la autenticación en el contexto de Spring.
                    // El SecurityContextHolder es thread-local: vive solo durante esta petición.
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token inválido o vencido → dejar pasar sin autenticar.
            // Spring retornará 401 si el endpoint requiere autenticación.
            logger.warn("JWT inválido: " + e.getMessage());
        }

        // Paso 9: siempre pasar al siguiente filtro (nunca "atascar" la petición aquí)
        filterChain.doFilter(request, response);
    }
}
