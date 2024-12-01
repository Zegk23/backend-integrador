package com.backend.integrador.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.backend.integrador.Repository.CategoriaRepositorio;
import com.backend.integrador.Models.Categoria;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaRepositorio categoriaRepositorio;

    @GetMapping("/obtenerCategorias")
    public List<Categoria> getAllCategorias() {
        return categoriaRepositorio.findAll();
    }
}
