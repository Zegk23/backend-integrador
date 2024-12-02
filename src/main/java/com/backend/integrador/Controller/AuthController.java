package com.backend.integrador.Controller;

import com.backend.integrador.DTO.Login;
import com.backend.integrador.Models.Usuario;
import com.backend.integrador.Security.LoginAttemptService;
import com.backend.integrador.Service.UsuarioService;
import com.backend.integrador.Util.JWTUtil;
import com.google.common.collect.ImmutableMap;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login login, HttpServletRequest request) {
        // Sanitizar entrada contra XSS
        String sanitizedCorreo = Jsoup.clean(login.getCorreo(), Safelist.basic());
        String sanitizedPassword = Jsoup.clean(login.getPassword(), Safelist.none());

        String key = sanitizedCorreo;

        if (loginAttemptService.isBlocked(key)) {
            return ResponseEntity.status(403)
                    .body("Tu cuenta está bloqueada temporalmente. Intenta nuevamente después de 15 minutos.");
        }

        try {
            Optional<Usuario> user = usuarioService.verificarUsuario(sanitizedCorreo, sanitizedPassword);
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
                        ip);

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
            return ResponseEntity.badRequest().body(ImmutableMap.of("error", "El token de autorización es obligatorio"));
        }
    
        try {
            String jwtToken = token.replace("Bearer ", "");
            String ip = Optional.ofNullable(request.getHeader("X-Forwarded-For")).orElse(request.getRemoteAddr());
    
            if (!jwtUtil.validarTokenConIP(jwtToken, ip)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ImmutableMap.of("error", "Token inválido o utilizado desde una ubicación no autorizada."));
            }
    
            String correo = jwtUtil.getCorreoFromToken(jwtToken);
            Optional<Usuario> user = usuarioService.obtenerUsuarioPorCorreo(correo);
    
            return user.map(usuario -> {
                String sanitizedNombre = Jsoup.clean(updatedUser.getNombre(), Safelist.basic());
                String sanitizedApellido = Jsoup.clean(updatedUser.getApellido(), Safelist.basic());
                String sanitizedTelefono = Jsoup.clean(updatedUser.getTelefono(), Safelist.none());
    
                if (sanitizedNombre == null || sanitizedNombre.isEmpty()) {
                    return ResponseEntity.badRequest().body(ImmutableMap.of("error", "El nombre no puede estar vacío"));
                }
                if (sanitizedApellido == null || sanitizedApellido.isEmpty()) {
                    return ResponseEntity.badRequest().body(ImmutableMap.of("error", "El apellido no puede estar vacío"));
                }
    
                usuario.setNombre(sanitizedNombre);
                usuario.setApellido(sanitizedApellido);
                usuario.setTelefono(sanitizedTelefono);
    
                usuarioService.actualizarUsuario(usuario);
    
                return ResponseEntity.ok(ImmutableMap.of("mensaje", "Datos del usuario actualizados exitosamente"));
            }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ImmutableMap.of("error", "Usuario no encontrado")));
    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ImmutableMap.of("error", "Error al actualizar los datos del usuario"));
        }
    }
    
}
