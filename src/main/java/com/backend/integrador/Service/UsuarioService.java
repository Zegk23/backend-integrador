package com.backend.integrador.Service;

import com.backend.integrador.Models.Usuario;
import com.backend.integrador.Models.Rol;
import com.backend.integrador.Repository.UsuarioRepositorio;
import com.backend.integrador.Repository.RolRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;  

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private RolRepositorio rolRepositorio;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Usuario registrarUsuario(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        Rol rolPorDefecto = rolRepositorio.findById(2L)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        usuario.setRol(rolPorDefecto);

        return usuarioRepositorio.save(usuario);
    }

    public Optional<Usuario> verificarUsuario(String correo, String password) {
        Optional<Usuario> usuario = usuarioRepositorio.findByCorreo(correo); 
        if (usuario.isPresent() && passwordEncoder.matches(password, usuario.get().getPassword())) {
            return usuario; // Retorna el usuario si la contraseña coincide
        }
        return Optional.empty(); // Retorna vacío si no coincide
    }
}
