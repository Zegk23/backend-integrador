package com.backend.integrador.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.integrador.Models.Usuario;

public interface UsuarioRepositorio extends JpaRepository<Usuario,Long> {
    
}
