package com.backend.integrador;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.backend.integrador.Util.JWTUtil;

public class JWTUtilTest {

    private JWTUtil jwtUtil;
    private String token;

    @BeforeEach
    public void setUp() {
        jwtUtil = new JWTUtil();
        token = jwtUtil.generarToken("user@example.com");
    }

    @Test
    public void testGenerarToken() {
        assertNotNull(token, "El token no debe ser nulo");
    }

    @Test
    public void testValidarToken() {
        assertTrue(jwtUtil.validarToken(token), "El token debería ser válido");
    }

    @Test
    public void testGetSubject() {
        String subject = jwtUtil.getSubject(token);
        assertEquals("user@example.com", subject, "El sujeto del token debe coincidir con el valor esperado");
    }

    @Test
    public void testTokenInvalido() {
        String invalidToken = token + "invalid";
        assertFalse(jwtUtil.validarToken(invalidToken), "El token manipulado no debería ser válido");
    }
}
