package com.backend.integrador.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.integrador.Models.MetodoPago;

public interface MetodoPagoRepositorio extends JpaRepository<MetodoPago,Long> {
    
}
