package com.backend.integrador.Repository;

import com.backend.integrador.Models.RecojoTienda;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecojoTiendaRepositorio extends JpaRepository<RecojoTienda, Long> {
    Optional<RecojoTienda> findByPedidoId(Long pedidoId);
}
