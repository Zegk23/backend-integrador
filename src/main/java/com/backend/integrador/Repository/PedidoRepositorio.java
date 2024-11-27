package com.backend.integrador.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.integrador.Models.Pedido;

public interface PedidoRepositorio extends JpaRepository<Pedido, Long> {
}
