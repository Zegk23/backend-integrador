package com.backend.integrador.Controller;

import com.backend.integrador.DTO.Login;
import com.backend.integrador.Models.Usuario;
import com.backend.integrador.Service.UsuarioService;
import com.backend.integrador.Util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.MessagingException;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> registrar(@RequestBody Usuario usuario) {
        System.out.println("Datos recibidos: " + usuario.toString());

        if (usuario.getNombre() == null || usuario.getNombre().isEmpty()) {
            return ResponseEntity.badRequest().body("El nombre es obligatorio");
        }

        try {
            usuarioService.registrarUsuario(usuario);
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Error al enviar el correo de bienvenida.");
        }

        return ResponseEntity.ok("Usuario registrado exitosamente");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Login login) {
        try {
            Optional<Usuario> user = usuarioService.verificarUsuario(login.getCorreo(), login.getPassword());
            if (user.isPresent()) {
                String token = jwtUtil.generarToken(user.get().getCorreo());
                return ResponseEntity.ok(token);
            }
            return ResponseEntity.badRequest().body("Correo o contraseña incorrectos");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Error al enviar el correo de inicio de sesión.");
        }
    }
}
