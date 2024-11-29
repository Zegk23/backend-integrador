package com.backend.integrador.Service;

import com.backend.integrador.Models.HistorialPedido;
import com.backend.integrador.Models.PedidoProducto;
import com.backend.integrador.Models.Producto;
import com.backend.integrador.Repository.HistorialPedidoRepositorio;
import com.backend.integrador.Repository.ProductoRepositorio; // Asegúrate de importar el repositorio adecuado

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistorialPedidoService {

    @Autowired
    private HistorialPedidoRepositorio historialPedidoRepositorio;

    @Autowired
    private ProductoRepositorio productoRepository; // Asegúrate de que el repositorio está bien configurado

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
                historialPedido.setProductoId(producto.getId());  // Usar setProductoId aquí
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
}
