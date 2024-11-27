package com.backend.integrador.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.backend.integrador.Models.Direccion;
import com.backend.integrador.Models.Usuario;

@Repository
public interface DireccionRepositorio extends JpaRepository<Direccion, Long> {
    Optional<Direccion> findByUsuarioAndDireccionAndCiudadAndCodigoPostalAndPais(
            Usuario usuario, String direccion, String ciudad, String codigoPostal, String pais);

    Optional<Direccion> findByDireccionIgnoreCase(String direccion);

}
