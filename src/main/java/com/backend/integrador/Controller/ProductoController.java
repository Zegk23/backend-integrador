package com.backend.integrador.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.backend.integrador.Models.Producto;
import com.backend.integrador.Repository.ProductoRepositorio;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductoController {

    @Autowired
    private ProductoRepositorio productoRepositorio;

    @GetMapping("/listarProductos")
    public List<Producto> listarProductos() {
        return productoRepositorio.findAll();
    }

    @GetMapping("/masVendidos")
    public List<Producto> obtenerTop4ProductosMasVendidos() {
        return productoRepositorio.findTop4ProductosMasVendidos();
    }
}
