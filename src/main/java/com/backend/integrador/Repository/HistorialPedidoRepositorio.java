package com.backend.integrador.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.integrador.Models.HistorialPedido;

public interface HistorialPedidoRepositorio extends JpaRepository<HistorialPedido,Long> {
    
}
