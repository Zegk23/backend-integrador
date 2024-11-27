package com.backend.integrador.Service;

import com.backend.integrador.Exceptions.StockInsuficienteException;
import com.backend.integrador.Models.*;
import com.backend.integrador.Repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PedidoService {

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private PedidoProductoRepositorio pedidoProductoRepositorio;

    @Autowired
    private ProductoRepositorio productoRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private DireccionRepositorio direccionRepositorio;

    @Autowired
    private RecojoTiendaRepositorio recojoTiendaRepositorio;

    public Pedido crearPedido(Pedido pedido, List<PedidoProducto> productos, RecojoTienda recojoTienda) {
        log.info("Iniciando creación del pedido...");
        // Validar usuario
        Usuario usuario = usuarioRepositorio.findById(pedido.getUsuario().getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        log.info("Usuario validado: {}", usuario.getId());
        pedido.setUsuario(usuario);

        // Manejo de dirección o recojo en tienda
        if (pedido.getDireccion() == null && recojoTienda != null) {
            log.info("Procesando pedido para recojo en tienda...");
            Direccion direccionTienda;
            if ("Velazco Av Grau 199".equalsIgnoreCase(recojoTienda.getLocal())) {
                direccionTienda = direccionRepositorio.findById(1L)
                        .orElseThrow(() -> new IllegalArgumentException("Dirección Velazco Av Grau 199 no encontrada"));
            } else if ("Velazco Megaplaza".equalsIgnoreCase(recojoTienda.getLocal())) {
                direccionTienda = direccionRepositorio.findById(2L)
                        .orElseThrow(() -> new IllegalArgumentException("Dirección Velazco Megaplaza no encontrada"));
            } else {
                throw new IllegalArgumentException("Local de recojo no válido");
            }

            log.info("Dirección de recojo asignada: {}", direccionTienda.getDireccion());
            pedido.setDireccion(direccionTienda);

            // Guardar los detalles del recojo en la tabla RecojoTienda
            recojoTienda.setPedido(pedido);
            recojoTiendaRepositorio.save(recojoTienda);
            log.info("Detalles del recojo guardados en la base de datos");
        } else if (pedido.getDireccion() != null) {
            log.info("Procesando pedido para delivery...");
            Direccion direccionPedido = pedido.getDireccion();
            Direccion direccion = direccionRepositorio.findByUsuarioAndDireccionAndCiudadAndCodigoPostalAndPais(
                    usuario,
                    direccionPedido.getDireccion(),
                    direccionPedido.getCiudad(),
                    direccionPedido.getCodigoPostal(),
                    direccionPedido.getPais())
                    .orElseGet(() -> {
                        // Si no existe, guardar la nueva dirección
                        log.info("Nueva dirección detectada, guardando...");
                        direccionPedido.setUsuario(usuario);
                        return direccionRepositorio.save(direccionPedido);
                    });

            pedido.setDireccion(direccion);
            log.info("Dirección para delivery asignada: {}", direccion.getDireccion());
        }

        // Manejo de productos
        log.info("Procesando productos del pedido...");
        List<PedidoProducto> productosProcesados = new ArrayList<>();
        for (PedidoProducto pedidoProducto : productos) {
            Producto producto = productoRepositorio.findById(pedidoProducto.getProducto().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + pedidoProducto.getProducto().getId()));

            // Verificar stock
            if (producto.getStock() < pedidoProducto.getCantidad()) {
                throw new StockInsuficienteException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            // Actualizar detalles del producto
            pedidoProducto.setProducto(producto);
            pedidoProducto.setSubtotal(producto.getPrecio() * pedidoProducto.getCantidad());
            producto.setStock(producto.getStock() - pedidoProducto.getCantidad());
            productoRepositorio.save(producto);

            productosProcesados.add(pedidoProducto);
            log.info("Producto procesado: {} - Cantidad: {}", producto.getNombre(), pedidoProducto.getCantidad());
        }

        // Asociar productos al pedido
        for (PedidoProducto pedidoProducto : productosProcesados) {
            pedido.agregarPedidoProducto(pedidoProducto);
        }

        // Guardar y retornar el pedido
        Pedido pedidoGuardado = pedidoRepositorio.save(pedido);
        log.info("Pedido creado con éxito. ID del pedido: {}", pedidoGuardado.getId());
        return pedidoGuardado;
    }
}
