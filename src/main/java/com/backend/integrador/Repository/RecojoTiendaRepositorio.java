package com.backend.integrador.Repository;

import com.backend.integrador.Models.RecojoTienda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecojoTiendaRepositorio extends JpaRepository<RecojoTienda, Long> {
}
