package com.Usuario.ms.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// =============================================================
// LÓGICA JWT — JwtUtil.java
// =============================================================
// Genera y valida tokens JWT para HelpTata.
// Un token tiene tres partes: header.payload.firma
//
// El payload incluye:
//   - subject: email del usuario (identificador principal)
//   - rut:     RUT completo con dígito verificador
//   - rol:     rol del usuario (ej: "USER", "ADMIN")
//   - iat:     fecha de emisión
//   - exp:     fecha de expiración (24h por defecto)
//
// ¡IMPORTANTE! El payload es legible por cualquiera (no cifrado, solo firmado).
//              Nunca incluyas contraseñas u otros datos sensibles en el token.
// =============================================================
@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration}")
    private long expiration;

    // Convierte el string secreto en una clave criptográfica HMAC-SHA
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Genera el token JWT con email como subject y todos los datos del usuario como claims
    public String generateToken(UserDetails userDetails, String rut, String rol, int id, String nombre) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rut", rut);
        claims.put("rol", rol);
        claims.put("id", id);
        claims.put("nombre", nombre);
        return buildToken(claims, userDetails.getUsername());
    }

    private String buildToken(Map<String, Object> extraClaims, String subject) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    // Extrae el email del token (es el subject del JWT)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extrae el RUT del payload del token
    public String extractRut(String token) {
        return extractClaim(token, claims -> claims.get("rut", String.class));
    }

    // Extrae el rol del payload del token
    public String extractRol(String token) {
        return extractClaim(token, claims -> claims.get("rol", String.class));
    }

    // Validación completa: verifica que el email coincida y que el token no haya expirado
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Patrón genérico para extraer cualquier campo del payload con una lambda
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    // Parsea y verifica la firma del token. Si alguien lo alteró, JJWT lanza excepción.
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
