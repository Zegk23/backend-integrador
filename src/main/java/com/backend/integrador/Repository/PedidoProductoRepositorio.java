package com.backend.integrador.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.integrador.Models.Pedido;
import com.backend.integrador.Models.PedidoProducto;

public interface PedidoProductoRepositorio extends JpaRepository<PedidoProducto,Long> {
    List<PedidoProducto> findByPedido(Pedido pedido);
}
