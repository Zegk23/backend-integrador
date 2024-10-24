package com.backend.integrador.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.integrador.Models.Contacto;

public interface ContactoRepositorio  extends JpaRepository<Contacto,Long>{
    
}
