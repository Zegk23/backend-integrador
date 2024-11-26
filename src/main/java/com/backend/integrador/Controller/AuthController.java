package com.backend.integrador.Controller;

import com.backend.integrador.DTO.Login;
import com.backend.integrador.Models.Usuario;
import com.backend.integrador.Service.UsuarioService;
import com.backend.integrador.Util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.common.collect.ImmutableMap;
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
    public ResponseEntity<?> login(@RequestBody Login login) {
        try {
            Optional<Usuario> user = usuarioService.verificarUsuario(login.getCorreo(), login.getPassword());
            if (user.isPresent()) {
                String token = jwtUtil.generarToken(user.get().getCorreo());
                String nombre = user.get().getNombre();
                String apellido = user.get().getApellido();
                String correo = user.get().getCorreo();
                String telefono = user.get().getTelefono();

                // Usar ImmutableMap para devolver token y detalles adicionales del usuario
                ImmutableMap<String, String> response = ImmutableMap.of(
                        "token", token,
                        "nombre", nombre,
                        "apellido", apellido,
                        "correo", correo,
                        "telefono", telefono);

                return ResponseEntity.ok(response);
            }
            return ResponseEntity.badRequest().body("Correo o contraseña incorrectos");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Error al enviar el correo de inicio de sesión.");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> actualizarUsuario(@RequestBody Usuario updatedUser,
            @RequestHeader("Authorization") String token) {
        try {
            String correo = jwtUtil.getCorreoFromToken(token.replace("Bearer ", ""));

            Optional<Usuario> user = usuarioService.obtenerUsuarioPorCorreo(correo);
            if (user.isPresent()) {
                Usuario usuario = user.get();
                usuario.setNombre(updatedUser.getNombre());
                usuario.setApellido(updatedUser.getApellido());
                usuario.setCorreo(updatedUser.getCorreo());
                usuario.setTelefono(updatedUser.getTelefono());

                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    usuarioService.actualizarContrasena(usuario, updatedUser.getPassword());
                }

                usuarioService.actualizarUsuario(usuario);
                return ResponseEntity.ok("Datos del usuario actualizados exitosamente");
            }
            return ResponseEntity.status(404).body("Usuario no encontrado");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar los datos del usuario");
        }
    }
}
