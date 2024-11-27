package com.backend.integrador.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.integrador.Models.TipoEntrega;

public interface TipoEntregaRepositorio extends JpaRepository<TipoEntrega, Long> {
}
