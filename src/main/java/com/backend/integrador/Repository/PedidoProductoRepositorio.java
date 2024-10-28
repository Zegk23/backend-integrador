package com.backend.integrador.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.integrador.Models.PedidoProducto;

public interface PedidoProductoRepositorio extends JpaRepository<PedidoProducto,Long> {
    
}
