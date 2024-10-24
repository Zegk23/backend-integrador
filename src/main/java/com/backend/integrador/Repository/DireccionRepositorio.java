package com.backend.integrador.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.integrador.Models.Direccion;

public interface DireccionRepositorio extends JpaRepository<Direccion,Long> {
    
}
