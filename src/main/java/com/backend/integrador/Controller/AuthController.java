package com.backend.integrador.Controller;

import com.backend.integrador.DTO.Login;
import com.backend.integrador.Models.Usuario;
import com.backend.integrador.Security.LoginAttemptService;
import com.backend.integrador.Service.UsuarioService;
import com.backend.integrador.Util.JWTUtil;
import com.google.common.collect.ImmutableMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login login, HttpServletRequest request) {
        String key = login.getCorreo();

        if (loginAttemptService.isBlocked(key)) {
            return ResponseEntity.status(403)
                    .body("Tu cuenta está bloqueada temporalmente. Intenta nuevamente después de 15 minutos.");
        }

        try {
            Optional<Usuario> user = usuarioService.verificarUsuario(login.getCorreo(), login.getPassword());
            if (user.isPresent()) {
                loginAttemptService.loginSucceeded(key);

                Usuario usuario = user.get();

                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty()) {
                    ip = request.getRemoteAddr();
                }

                String token = jwtUtil.generarToken(
                        usuario.getId(),
                        usuario.getNombre(),
                        usuario.getApellido(),
                        usuario.getCorreo(),
                        usuario.getTelefono(),
                        usuario.getRol().getId(),
                        ip
                );

                return ResponseEntity.ok(ImmutableMap.of("token", token));
            }

            loginAttemptService.loginFailed(key);
            int remainingAttempts = loginAttemptService.getRemainingAttempts(key);

            return ResponseEntity.badRequest()
                    .body("Correo o contraseña incorrectos. Intentos restantes: " + remainingAttempts);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al procesar la solicitud de inicio de sesión");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> actualizarUsuario(@RequestBody Usuario updatedUser,
                                               @RequestHeader("Authorization") String token,
                                               HttpServletRequest request) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body("El token de autorización es obligatorio");
        }

        try {
            String jwtToken = token.replace("Bearer ", "");
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty()) {
                ip = request.getRemoteAddr();
            }

            // Validar token y comparar la IP
            if (!jwtUtil.validarTokenConIP(jwtToken, ip)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Token inválido o utilizado desde una ubicación no autorizada.");
            }

            String correo = jwtUtil.getCorreoFromToken(jwtToken);
            Optional<Usuario> user = usuarioService.obtenerUsuarioPorCorreo(correo);

            return user.map(usuario -> {
                if (updatedUser.getNombre() == null || updatedUser.getNombre().isEmpty()) {
                    return ResponseEntity.badRequest().body("El nombre no puede estar vacío");
                }
                if (updatedUser.getApellido() == null || updatedUser.getApellido().isEmpty()) {
                    return ResponseEntity.badRequest().body("El apellido no puede estar vacío");
                }

                usuario.setNombre(updatedUser.getNombre());
                usuario.setApellido(updatedUser.getApellido());
                usuario.setTelefono(updatedUser.getTelefono());

                if (!usuario.getCorreo().equals(updatedUser.getCorreo())) {
                    usuario.setCorreo(updatedUser.getCorreo());
                }

                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    usuarioService.actualizarContrasena(usuario, updatedUser.getPassword());
                }

                usuarioService.actualizarUsuario(usuario);
                return ResponseEntity.ok("Datos del usuario actualizados exitosamente");
            }).orElse(ResponseEntity.status(404).body("Usuario no encontrado"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar los datos del usuario");
        }
    }
}
