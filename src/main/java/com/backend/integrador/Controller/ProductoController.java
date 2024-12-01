package com.backend.integrador.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.backend.integrador.Models.Producto;
import com.backend.integrador.Service.ProductoService;
import com.backend.integrador.Repository.ProductoRepositorio;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")

public class ProductoController {

    @Autowired
    private ProductoRepositorio productoRepositorio;

    @Autowired
    private ProductoService productoService;

    @PostMapping("/crearProducto")
    public Producto crearProducto(@RequestBody Producto producto) {
        return productoRepositorio.save(producto); 
    }

    @GetMapping("/listarProductos")
    public List<Producto> listarProductos() {
        return productoRepositorio.findAll();
    }

    @GetMapping("/masVendidos")
    public List<Producto> obtenerTop4ProductosMasVendidos() {
        return productoRepositorio.findTop4ProductosMasVendidos();
    }

    @GetMapping("/obtenerPorId/{id}")
    public Optional<Producto> obtenerProductoID(@PathVariable Long id) {
        return productoService.obtenerProductoPorId(id);
    }
}
