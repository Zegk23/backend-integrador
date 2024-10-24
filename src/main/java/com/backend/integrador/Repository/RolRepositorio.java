package com.backend.integrador.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.integrador.Models.Rol;

public interface RolRepositorio extends JpaRepository<Rol,Long>{
    
}
