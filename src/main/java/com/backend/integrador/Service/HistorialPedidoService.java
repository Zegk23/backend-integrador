package com.backend.integrador.Service;

import com.backend.integrador.Models.*;
import com.backend.integrador.Repository.HistorialPedidoRepositorio;
import com.backend.integrador.Repository.ProductoRepositorio;
import com.backend.integrador.Repository.RecojoTiendaRepositorio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HistorialPedidoService {

    @Autowired
    private HistorialPedidoRepositorio historialPedidoRepositorio;

    @Autowired
    private ProductoRepositorio productoRepositorio;

    @Autowired
    private RecojoTiendaRepositorio recojoTiendaRepositorio;

    @Transactional
    public void registrarHistorialPedido(List<PedidoProducto> pedidoProductos) {
        for (PedidoProducto pedidoProducto : pedidoProductos) {
            HistorialPedido historialPedido = new HistorialPedido();

            // Establecer el pedido y el usuario directamente desde PedidoProducto
            historialPedido.setPedido(pedidoProducto.getPedido());
            historialPedido.setUsuario(pedidoProducto.getPedido().getUsuario());

            // Obtener el producto y asignarlo al historialPedido
            Producto producto = pedidoProducto.getProducto();
            if (producto != null) {
                historialPedido.setProductoId(producto.getId());
            } else {
                throw new IllegalArgumentException("Producto no encontrado para el pedido.");
            }

            // Asignar cantidad y subtotal
            historialPedido.setCantidad(pedidoProducto.getCantidad());
            historialPedido.setSubtotal(pedidoProducto.getSubtotal());

            // Guardar el historial en la base de datos
            historialPedidoRepositorio.save(historialPedido);
        }
    }

    public List<HistorialPedido> obtenerHistorialPorUsuario(Long usuarioId) {
        return historialPedidoRepositorio.findByUsuarioId(usuarioId);
    }

    public Map<String, Object> obtenerDetallePedido(Long pedidoId) {
        List<HistorialPedido> productosPedido = historialPedidoRepositorio.findByPedidoId(pedidoId);

        if (productosPedido.isEmpty()) {
            return null;
        }

        Pedido pedido = productosPedido.get(0).getPedido();

        String metodoEntrega;
        String detalleEntrega;

        if (pedido.getDireccion() != null) {
            if (pedido.getDireccion().getId() == 1 || pedido.getDireccion().getId() == 2) {
                metodoEntrega = "Recojo en Tienda";
                RecojoTienda recojoTienda = recojoTiendaRepositorio.findByPedidoId(pedidoId).orElse(null);
                if (recojoTienda != null) {
                    detalleEntrega = String.format("Local: %s, Horario: %s",
                            recojoTienda.getLocal(),
                            recojoTienda.getHorario());
                } else {
                    detalleEntrega = "Información de recojo en tienda no disponible.";
                }
            } else {
                metodoEntrega = "Delivery";
                Direccion direccion = pedido.getDireccion();
                detalleEntrega = String.format("Dirección: %s, Ciudad: %s, Código Postal: %s, País: %s",
                        direccion.getDireccion(),
                        direccion.getCiudad(),
                        direccion.getCodigoPostal(),
                        direccion.getPais());
            }
        } else {
            metodoEntrega = "Desconocido";
            detalleEntrega = "Información de entrega no disponible.";
        }

        String fechaPedido = pedido.getFecha() != null ? pedido.getFecha() : "Fecha no disponible";

        double totalPedido = productosPedido.stream()
                .mapToDouble(HistorialPedido::getSubtotal)
                .sum();

        Map<String, Object> detallePedido = Map.of(
                "pedidoId", pedidoId,
                "estado", pedido.getEstado(),
                "fecha", fechaPedido,
                "metodoEntrega", metodoEntrega,
                "detalleEntrega", detalleEntrega,
                "total", totalPedido,
                "productos", productosPedido.stream().map(pedidoProducto -> {
                    Producto producto = productoRepositorio.findById(pedidoProducto.getProductoId()).orElse(null);
                    return Map.of(
                            "productoId", pedidoProducto.getProductoId(),
                            "nombreProducto", producto != null ? producto.getNombre() : "Desconocido",
                            "imageUrl", producto != null ? producto.getImageUrl() : "",
                            "cantidad", pedidoProducto.getCantidad(),
                            "subtotal", pedidoProducto.getSubtotal());
                }).collect(Collectors.toList()));

        return detallePedido;
    }
}
