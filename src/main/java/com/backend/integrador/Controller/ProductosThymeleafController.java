package com.backend.integrador.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.backend.integrador.Models.Categoria;
import com.backend.integrador.Models.Producto;
import com.backend.integrador.Models.Proveedor;
import com.backend.integrador.Repository.CategoriaRepositorio;
import com.backend.integrador.Repository.ProductoRepositorio;
import com.backend.integrador.Repository.ProveedorRepositorio;

import java.util.Optional;

@Controller
@RequestMapping("/productos")
public class ProductosThymeleafController {

    @Autowired
    private ProductoRepositorio productoRepositorio;

    @Autowired
    private CategoriaRepositorio categoriaRepositorio;

    @Autowired
    private ProveedorRepositorio proveedorRepositorio;

    // Listar todos los productos
    @GetMapping("/listar")
    public String listarProductos(Model model) {
        model.addAttribute("productos", productoRepositorio.findAll());
        return "productosListar";
    }

    // Mostrar el formulario para agregar un nuevo producto
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoProducto(Model model) {
        Producto producto = new Producto();
        List<Categoria> categorias = categoriaRepositorio.findAll();
        List<Proveedor> proveedores = proveedorRepositorio.findAll();

        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categorias); 
        model.addAttribute("proveedores", proveedores); 
        return "productoForm";
    }

    // Guardar un nuevo producto
    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute("producto") Producto producto,
            RedirectAttributes redirectAttributes) {
        productoRepositorio.save(producto);
        redirectAttributes.addFlashAttribute("mensaje", "Producto agregado exitosamente");
        return "redirect:/productos/listar";
    }

    // Mostrar el formulario para editar un producto
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarProducto(@PathVariable Long id, Model model) {
        Optional<Producto> producto = productoRepositorio.findById(id);
        if (producto.isPresent()) {
            List<Categoria> categorias = categoriaRepositorio.findAll();
            List<Proveedor> proveedores = proveedorRepositorio.findAll();

            model.addAttribute("producto", producto.get());
            model.addAttribute("categorias", categorias); 
            model.addAttribute("proveedores", proveedores);
            return "productoForm";
        } else {
            return "redirect:/productos/listar";
        }
    }

    // Actualizar un producto existente
    @PostMapping("/actualizar/{id}")
    public String actualizarProducto(@PathVariable Long id, @ModelAttribute("producto") Producto producto,
            RedirectAttributes redirectAttributes) {
        Optional<Producto> productoExistente = productoRepositorio.findById(id);
        if (productoExistente.isPresent()) {
            Producto productoActual = productoExistente.get();
            productoActual.setNombre(producto.getNombre());
            productoActual.setImageUrl(producto.getImageUrl());
            productoActual.setPrecio(producto.getPrecio());
            productoActual.setStock(producto.getStock());
            productoActual.setCategoria(producto.getCategoria());
            productoActual.setProveedor(producto.getProveedor());
            productoRepositorio.save(productoActual);
            redirectAttributes.addFlashAttribute("mensaje", "Producto actualizado exitosamente");
        } else {
            redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
        }
        return "redirect:/productos/listar";
    }

    // Eliminar un producto
    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Producto> producto = productoRepositorio.findById(id);
        if (producto.isPresent()) {
            productoRepositorio.delete(producto.get());
            redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado exitosamente");
        } else {
            redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
        }
        return "redirect:/productos/listar";
    }
}
