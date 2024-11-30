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

    private Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generarToken(Long userId, String nombre, String apellido, String correo, String telefono,
                               Long rolId, String ip) {
        return Jwts.builder()
                .setSubject(correo)
                .claim("userId", userId)
                .claim("nombre", nombre)
                .claim("apellido", apellido)
                .claim("telefono", telefono)
                .claim("rol_id", rolId)
                .claim("ip", ip) // Agregar IP al token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // Token válido por 24 horas
                .signWith(key)
                .compact();
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean validarTokenConIP(String token, String ipActual) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String ipToken = claims.get("ip", String.class); // Obtener IP del token
            return ipToken.equals(ipActual); // Validar que coincidan
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getCorreoFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public Long getUserId(String token) {
        Claims claims = getClaims(token); // Obtiene todos los datos del token
        return claims.get("userId", Long.class); // Devuelve el userId como un Long
    }
    
}
