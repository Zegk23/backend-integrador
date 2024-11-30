package com.backend.integrador.Util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;

@Component
public class JWTUtil {

    // Llave secreta para firmar el token
    private Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     * Genera un token JWT con la información del usuario
     */
    public String generarToken(Long userId, String nombre, String apellido, String correo, String telefono,
            Long rolId) {
        return Jwts.builder()
                .setSubject(correo) // El correo como subject (identificación principal)
                .claim("userId", userId) // ID del usuario
                .claim("nombre", nombre) // Nombre del usuario
                .claim("apellido", apellido) // Apellido del usuario
                .claim("telefono", telefono) // Teléfono del usuario
                .claim("rol_id", rolId) // Rol del usuario
                .setIssuedAt(new Date()) // Fecha de emisión
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // Expira en 24 horas
                .signWith(key) // Firmar con la llave secreta
                .compact();
    }

    /**
     * Valida si el token es correcto y no ha expirado
     */
    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene el correo (subject) del token
     */
    public String getSubject(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Extrae todos los claims (datos) del token
     */
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getCorreoFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key) // Asegúrate de que `key` sea tu clave de firma
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject(); // Aquí asumimos que el correo está en el "subject"
    }

    public Long getUserId(String token) {
        return getClaims(token).get("userId", Long.class);
    }

    public String getNombre(String token) {
        return getClaims(token).get("nombre", String.class);
    }

    public String getApellido(String token) {
        return getClaims(token).get("apellido", String.class);
    }

    public String getTelefono(String token) {
        return getClaims(token).get("telefono", String.class);
    }

    public Long getRolId(String token) {
        return getClaims(token).get("rol_id", Long.class);
    }
}
