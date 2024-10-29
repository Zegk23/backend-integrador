package com.backend.integrador.Service;

import com.backend.integrador.Models.Producto;
import com.backend.integrador.Repository.ProductoRepositorio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {
    @Autowired
    private final ProductoRepositorio productoRepositorio;

    public ProductoService(ProductoRepositorio productoRepositorio) {
        this.productoRepositorio = productoRepositorio;
    }

    public List<Producto> obtenerTop4ProductosMasVendidos() {
        return productoRepositorio.findTop4ProductosMasVendidos();
    }

    public Optional<Producto> obtenerProductoPorId(Long id){
        return productoRepositorio.findById(id);
    }
}
