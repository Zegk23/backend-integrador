package com.backend.integrador.Service;

import com.backend.integrador.Models.Usuario;
import com.backend.integrador.Exceptions.CorreoElectronicoYaExiste;
import com.backend.integrador.Models.Rol;
import com.backend.integrador.Repository.UsuarioRepositorio;
import com.backend.integrador.Repository.RolRepositorio;
import com.google.common.base.Preconditions;
import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Slf4j
public class UsuarioService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private RolRepositorio rolRepositorio;

    @Autowired
    private CorreosService correosService;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private String hashPassword(String password) {
        return Hashing.sha256()
                .hashString(password, StandardCharsets.UTF_8)
                .toString();
    }

    public Optional<Usuario> obtenerUsuarioPorCorreo(String correo) {
        log.info("Buscando usuario con correo: {}", correo);
        return usuarioRepositorio.findByCorreo(correo);
    }

    public Usuario registrarUsuario(Usuario usuario) throws MessagingException {
        log.info("Iniciando registro de usuario con correo: {}", usuario.getCorreo());

        validarUsuario(usuario);

        if (usuarioRepositorio.findByCorreo(usuario.getCorreo()).isPresent()) {
            log.warn("El correo electrónico {} ya está registrado", usuario.getCorreo());
            throw new CorreoElectronicoYaExiste("El correo electrónico ya está registrado");
        }

        usuario.setPassword(hashPassword(usuario.getPassword()));

        Rol rolPorDefecto = rolRepositorio.findById(2L)
                .orElseThrow(() -> {
                    log.error("Rol por defecto no encontrado");
                    return new RuntimeException("Rol no encontrado");
                });
        usuario.setRol(rolPorDefecto);

        Usuario savedUser = usuarioRepositorio.save(usuario);

        log.info("Usuario registrado exitosamente con ID: {}", savedUser.getId());

        correosService.sendEmail(
                usuario.getCorreo(),
                "Bienvenido a Velazco Panadería y Dulcería",
                "emailTemplate",
                usuario.getNombre()
        );

        log.info("Correo de bienvenida enviado al usuario: {}", usuario.getCorreo());
        return savedUser;
    }

    public Optional<Usuario> verificarUsuario(String correo, String password) throws MessagingException {
        log.info("Verificando usuario con correo: {}", correo);

        Optional<Usuario> usuario = usuarioRepositorio.findByCorreo(correo);
        if (usuario.isPresent() && usuario.get().getPassword().equals(hashPassword(password))) {
            log.info("Inicio de sesión exitoso para el usuario con correo: {}", correo);

            correosService.sendEmail(
                    correo,
                    "Inicio de sesión exitoso",
                    "emailTemplate",
                    usuario.get().getNombre()
            );

            log.info("Correo de inicio de sesión enviado al usuario: {}", correo);
            return usuario;
        }

        log.warn("Credenciales incorrectas para el correo: {}", correo);
        return Optional.empty();
    }

    private void validarUsuario(Usuario usuario) {
        log.info("Validando datos del usuario: {}", usuario.getCorreo());

        Preconditions.checkArgument(usuario.getNombre() != null && usuario.getNombre().length() >= 2,
                "El nombre debe tener al menos 2 caracteres");
        Preconditions.checkArgument(usuario.getApellido() != null && usuario.getApellido().length() >= 2,
                "El apellido debe tener al menos 2 caracteres");

        Preconditions.checkArgument(EMAIL_PATTERN.matcher(usuario.getCorreo()).matches(),
                "El correo electrónico no tiene un formato válido");

        Preconditions.checkArgument(usuario.getPassword().length() >= 8,
                "La contraseña debe tener al menos 8 caracteres");

        log.info("Datos del usuario validados correctamente: {}", usuario.getCorreo());
    }

    public void actualizarUsuario(Usuario usuario) {
        log.info("Actualizando datos del usuario con ID: {}", usuario.getId());
        usuarioRepositorio.save(usuario);
        log.info("Datos del usuario actualizados exitosamente: {}", usuario.getId());
    }

    public void actualizarContrasena(Usuario usuario, String nuevaContrasena) {
        log.info("Actualizando contraseña del usuario con ID: {}", usuario.getId());
        usuario.setPassword(hashPassword(nuevaContrasena));
        usuarioRepositorio.save(usuario);
        log.info("Contraseña del usuario con ID: {} actualizada exitosamente", usuario.getId());
    }
}
