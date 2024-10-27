package com.backend.integrador.Service;

import com.backend.integrador.Models.Usuario;
import com.backend.integrador.Exceptions.CorreoElectronicoYaExiste;
import com.backend.integrador.Models.Rol;
import com.backend.integrador.Repository.UsuarioRepositorio;
import com.backend.integrador.Repository.RolRepositorio;
import com.google.common.base.Preconditions; // Validaciones con Google Guava
import com.google.common.hash.Hashing; // Google Guava Hashing
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private RolRepositorio rolRepositorio;

    @Autowired
    private CorreosService correosService; 

    public Optional<Usuario> obtenerUsuarioPorCorreo(String correo) {
        return usuarioRepositorio.findByCorreo(correo);
    }
    

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private String hashPassword(String password) {
        return Hashing.sha256()
                .hashString(password, StandardCharsets.UTF_8)
                .toString();
    }

    public Usuario registrarUsuario(Usuario usuario) throws MessagingException {
        validateUser(usuario); 

        if (usuarioRepositorio.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new CorreoElectronicoYaExiste("El correo electrónico ya está registrado");
        }
        
        usuario.setPassword(hashPassword(usuario.getPassword()));

        Rol rolPorDefecto = rolRepositorio.findById(2L)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        usuario.setRol(rolPorDefecto);

        Usuario savedUser = usuarioRepositorio.save(usuario);

        correosService.sendEmail(
                usuario.getCorreo(),
                "Bienvenido a Velazco Panadería y Dulcería",
                "emailTemplate", 
                usuario.getNombre()
        );

        return savedUser;
    }

    public Optional<Usuario> verificarUsuario(String correo, String password) throws MessagingException {
        Optional<Usuario> usuario = usuarioRepositorio.findByCorreo(correo);
        if (usuario.isPresent() && usuario.get().getPassword().equals(hashPassword(password))) {
            correosService.sendEmail(
                    correo,
                    "Inicio de sesión exitoso",
                    "emailTemplate", 
                    usuario.get().getNombre()
            );
            return usuario;
        }
        return Optional.empty(); 
    }

    private void validateUser(Usuario usuario) {
        Preconditions.checkArgument(usuario.getNombre() != null && usuario.getNombre().length() >= 2,
                "El nombre debe tener al menos 2 caracteres");
        Preconditions.checkArgument(usuario.getApellido() != null && usuario.getApellido().length() >= 2,
                "El apellido debe tener al menos 2 caracteres");

        Preconditions.checkArgument(EMAIL_PATTERN.matcher(usuario.getCorreo()).matches(),
                "El correo electrónico no tiene un formato válido");

        Preconditions.checkArgument(usuario.getPassword().length() >= 8,
                "La contraseña debe tener al menos 8 caracteres");
    }
    
    public void actualizarUsuario(Usuario usuario) {
        usuarioRepositorio.save(usuario); 
    }
    
    public void actualizarContrasena(Usuario usuario, String nuevaContrasena) {
        usuario.setPassword(hashPassword(nuevaContrasena));
        usuarioRepositorio.save(usuario);
    }
    
}
