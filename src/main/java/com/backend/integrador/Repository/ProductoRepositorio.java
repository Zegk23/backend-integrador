package com.backend.integrador.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.integrador.Models.Producto;

public interface ProductoRepositorio extends JpaRepository<Producto, Long>{
    
}
