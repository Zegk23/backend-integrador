package com.backend.integrador.Repository;

import com.backend.integrador.DTO.HistorialPedidoDTO;
import com.backend.integrador.Models.HistorialPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialPedidoRepositorio extends JpaRepository<HistorialPedido, Long> {

    @Query("SELECT new com.backend.integrador.DTO.HistorialPedidoDTO(" +
            "hp.id, p.id, p.nombre, p.imageUrl, hp.cantidad, hp.subtotal, hp.pedido.fecha) " +
            "FROM HistorialPedido hp " +
            "JOIN Producto p ON hp.productoId = p.id " +
            "WHERE hp.usuario.id = :usuarioId")
    List<HistorialPedidoDTO> findHistorialWithProductDetailsByUsuarioId(Long usuarioId);
    
    List<HistorialPedido> findByUsuarioId(Long usuarioId);
    List<HistorialPedido> findByPedidoId(Long pedidoId);
}
