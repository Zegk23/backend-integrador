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

    public String generarToken(Long userId, String correo, String nombre, String telefono) {
        return Jwts.builder()
                .setSubject(correo) // El correo sigue siendo el subject
                .claim("userId", userId) // Agregar el ID del usuario como claim
                .claim("nombre", nombre) // Agregar el nombre
                .claim("telefono", telefono) // Agregar el teléfono
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // Expira en 24 horas
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

    public String getSubject(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getCorreoFromToken(String token) {
        Claims claims = Jwts.parserBuilder() 
                .setSigningKey(key) 
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}