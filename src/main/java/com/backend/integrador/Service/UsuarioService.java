package com.backend.integrador.Service;

import com.backend.integrador.Models.Usuario;
import com.backend.integrador.Exceptions.CorreoElectronicoYaExiste;
import com.backend.integrador.Models.Rol;
import com.backend.integrador.Repository.UsuarioRepositorio;
import com.backend.integrador.Repository.RolRepositorio;
import com.google.common.hash.Hashing; // Google Guava Hashing
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private RolRepositorio rolRepositorio;

    private String hashPassword(String password) {
        return Hashing.sha256()
                .hashString(password, StandardCharsets.UTF_8)
                .toString();
    }

    public Usuario registrarUsuario(Usuario usuario) {
        if (usuarioRepositorio.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new CorreoElectronicoYaExiste("El correo electrónico ya está registrado");
        }
        usuario.setPassword(hashPassword(usuario.getPassword()));

        Rol rolPorDefecto = rolRepositorio.findById(2L)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        usuario.setRol(rolPorDefecto);

        return usuarioRepositorio.save(usuario);
    }

    public Optional<Usuario> verificarUsuario(String correo, String password) {
        Optional<Usuario> usuario = usuarioRepositorio.findByCorreo(correo);
        if (usuario.isPresent() && usuario.get().getPassword().equals(hashPassword(password))) {
            return usuario; 
        }
        return Optional.empty(); 
    }
}
