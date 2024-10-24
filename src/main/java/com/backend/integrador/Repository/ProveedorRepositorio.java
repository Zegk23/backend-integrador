package com.backend.integrador.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.integrador.Models.Proveedor;

public interface ProveedorRepositorio extends JpaRepository<Proveedor,Long>{
    
}
