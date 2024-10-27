package com.backend.integrador.Controller;

import com.backend.integrador.DTO.Login;
import com.backend.integrador.Models.Usuario;
import com.backend.integrador.Service.UsuarioService;
import com.backend.integrador.Util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JWTUtil jwtUtil;

    // Endpoint para registrar un nuevo usuario
    @PostMapping("/register")
    public ResponseEntity<String> registrar(@RequestBody Usuario usuario) {
        System.out.println("Datos recibidos: " + usuario.toString());

        if (usuario.getNombre() == null || usuario.getNombre().isEmpty()) {
            return ResponseEntity.badRequest().body("El nombre es obligatorio");
        }

        usuarioService.registrarUsuario(usuario);
        return ResponseEntity.ok("Usuario registrado exitosamente");
    }

    // Endpoint para iniciar sesión y devolver un token JWT
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Login login) {
        Optional<Usuario> user = usuarioService.verificarUsuario(login.getCorreo(), login.getPassword());
        if (user.isPresent()) {
            String token = jwtUtil.generarToken(user.get().getCorreo());
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.badRequest().body("Correo o contraseña incorrectos");
    }
}
