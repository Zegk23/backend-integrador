package com.backend.integrador.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.integrador.Models.Usuario;

import java.util.Optional;

public interface UsuarioRepositorio extends JpaRepository<Usuario,Long> {
    Optional<Usuario> findByCorreo(String correo);
}
