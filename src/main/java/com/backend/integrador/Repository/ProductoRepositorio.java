package com.backend.integrador.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.backend.integrador.Models.Producto;

public interface ProductoRepositorio extends JpaRepository<Producto, Long> {

    @Query(value = "SELECT p.* " +
                   "FROM producto p " +
                   "JOIN pedido_producto pp ON p.id = pp.producto_id " +
                   "GROUP BY p.id " +
                   "ORDER BY SUM(pp.cantidad) DESC " +
                   "LIMIT 4", nativeQuery = true)
    List<Producto> findTop4ProductosMasVendidos();
}
