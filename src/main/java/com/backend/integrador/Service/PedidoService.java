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
            Direccion direccionTienda = direccionRepositorio.findByDireccionIgnoreCase(recojoTienda.getLocal())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Dirección para el local '" + recojoTienda.getLocal() + "' no encontrada"));
            log.info("Dirección de recojo encontrada: {}", direccionTienda.getDireccion());
            pedido.setDireccion(direccionTienda);

            // Guardar pedido antes de asociar detalles del recojo
            Pedido pedidoGuardado = pedidoRepositorio.save(pedido);

            // Guardar detalles del recojo
            recojoTienda.setPedido(pedidoGuardado);
            recojoTiendaRepositorio.save(recojoTienda);
            log.info("Detalles del recojo guardados en la base de datos.");

            return pedidoGuardado;
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
                        log.info("Nueva dirección detectada, guardando...");
                        direccionPedido.setUsuario(usuario);
                        return direccionRepositorio.save(direccionPedido);
                    });

            log.info("Dirección guardada con ID: {}", direccion.getId());
            pedido.setDireccion(direccion);
        }

        // Guardar pedido
        Pedido pedidoGuardado = pedidoRepositorio.save(pedido);
        log.info("Pedido guardado con ID: {}", pedidoGuardado.getId());

        // Manejo de productos
        log.info("Procesando productos del pedido...");
        List<PedidoProducto> productosProcesados = new ArrayList<>();
        for (PedidoProducto pedidoProducto : productos) {
            Producto producto = productoRepositorio.findById(pedidoProducto.getProducto().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + pedidoProducto.getProducto().getId()));

            if (producto.getStock() < pedidoProducto.getCantidad()) {
                throw new StockInsuficienteException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            pedidoProducto.setProducto(producto);
            pedidoProducto.setPedido(pedidoGuardado);
            pedidoProducto.setSubtotal(producto.getPrecio() * pedidoProducto.getCantidad());
            producto.setStock(producto.getStock() - pedidoProducto.getCantidad());
            productoRepositorio.save(producto);

            productosProcesados.add(pedidoProducto);
            log.info("Producto procesado: {} - Cantidad: {}", producto.getNombre(), pedidoProducto.getCantidad());
        }

        pedidoProductoRepositorio.saveAll(productosProcesados);

        log.info("Pedido completado con éxito. ID del pedido: {}", pedidoGuardado.getId());
        return pedidoGuardado;
    }
}
