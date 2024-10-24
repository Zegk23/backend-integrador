package com.backend.integrador.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.integrador.Models.Pago;

public interface PagoRepositorio extends JpaRepository <Pago,Long>{
    
}
