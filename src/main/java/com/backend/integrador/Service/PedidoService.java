package com.backend.integrador.Service;

import com.backend.integrador.Models.Pedido;
import com.backend.integrador.Models.PedidoProducto;
import com.backend.integrador.Models.Producto;
import com.backend.integrador.Repository.PedidoProductoRepositorio;
import com.backend.integrador.Repository.PedidoRepositorio;
import com.backend.integrador.Repository.ProductoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private PedidoProductoRepositorio pedidoProductoRepositorio;

    @Autowired
    private ProductoRepositorio productoRepositorio;

    public Pedido crearPedido(Pedido pedido, List<PedidoProducto> productos) {

        Pedido nuevoPedido = pedidoRepositorio.save(pedido);

        for (PedidoProducto pedidoProducto : productos) {
            Producto producto = pedidoProducto.getProducto();

            if (producto.getStock() < pedidoProducto.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            pedidoProducto.setSubtotal(producto.getPrecio() * pedidoProducto.getCantidad());
            producto.setStock(producto.getStock() - pedidoProducto.getCantidad());

            pedidoProducto.setPedido(nuevoPedido);
            pedidoProductoRepositorio.save(pedidoProducto);
            
            productoRepositorio.save(producto);
        }

        return nuevoPedido;
    }
}