package com.backend.integrador.Controller;

import com.backend.integrador.DTO.Login;
import com.backend.integrador.Models.Usuario;
import com.backend.integrador.Service.UsuarioService;
import com.backend.integrador.Util.JWTUtil;
import com.google.common.collect.ImmutableMap;

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

    /**
     * Endpoint para el login del usuario.
     * Devuelve únicamente el token generado para el usuario.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login login) {
        try {
            Optional<Usuario> user = usuarioService.verificarUsuario(login.getCorreo(), login.getPassword());
            if (user.isPresent()) {
                Usuario usuario = user.get();
                String token = jwtUtil.generarToken(
                        usuario.getId(),
                        usuario.getNombre(),
                        usuario.getApellido(),
                        usuario.getCorreo(),
                        usuario.getTelefono(),
                        usuario.getRol().getId());

                // Retornar el token como JSON
                return ResponseEntity.ok(ImmutableMap.of("token", token));
            }
            return ResponseEntity.badRequest().body("Correo o contraseña incorrectos");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al procesar la solicitud de inicio de sesión");
        }
    }

    /**
     * Endpoint para actualizar los datos del usuario autenticado.
     */
    @PutMapping("/update")
    public ResponseEntity<?> actualizarUsuario(@RequestBody Usuario updatedUser,
                                               @RequestHeader("Authorization") String token) {
        try {
            // Validar el token y extraer el correo
            if (token == null || token.isEmpty()) {
                return ResponseEntity.badRequest().body("El token de autorización es obligatorio");
            }

            String correo = jwtUtil.getCorreoFromToken(token.replace("Bearer ", ""));
            Optional<Usuario> user = usuarioService.obtenerUsuarioPorCorreo(correo);

            if (user.isPresent()) {
                Usuario usuario = user.get();

                // Validar datos recibidos
                if (updatedUser.getNombre() == null || updatedUser.getNombre().isEmpty()) {
                    return ResponseEntity.badRequest().body("El nombre no puede estar vacío");
                }
                if (updatedUser.getApellido() == null || updatedUser.getApellido().isEmpty()) {
                    return ResponseEntity.badRequest().body("El apellido no puede estar vacío");
                }

                // Actualizar los datos del usuario
                usuario.setNombre(updatedUser.getNombre());
                usuario.setApellido(updatedUser.getApellido());
                usuario.setTelefono(updatedUser.getTelefono());

                // Validar si el correo es diferente antes de actualizar
                if (!usuario.getCorreo().equals(updatedUser.getCorreo())) {
                    usuario.setCorreo(updatedUser.getCorreo());
                }

                // Si incluye contraseña, actualizar
                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    usuarioService.actualizarContrasena(usuario, updatedUser.getPassword());
                }

                // Guardar los cambios
                usuarioService.actualizarUsuario(usuario);

                return ResponseEntity.ok("Datos del usuario actualizados exitosamente");
            }

            return ResponseEntity.status(404).body("Usuario no encontrado");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar los datos del usuario");
        }
    }
}
