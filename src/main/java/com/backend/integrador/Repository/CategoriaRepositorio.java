package com.backend.integrador.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.integrador.Models.Categoria;

public interface CategoriaRepositorio  extends JpaRepository<Categoria, Long>{
    
}
