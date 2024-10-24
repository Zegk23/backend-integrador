package com.backend.integrador.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.integrador.Models.ConfirmacionPedido;

public interface ConfirmacionPedidoRepositorio extends JpaRepository<ConfirmacionPedido, Long> {
    
}
