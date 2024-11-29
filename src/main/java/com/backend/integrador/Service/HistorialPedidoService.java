package com.backend.integrador.Service;

import com.backend.integrador.Models.HistorialPedido;
import com.backend.integrador.Models.PedidoProducto;
import com.backend.integrador.Repository.HistorialPedidoRepositorio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistorialPedidoService {

    @Autowired
    private HistorialPedidoRepositorio historialPedidoRepositorio;

    public void registrarHistorialPedido(List<PedidoProducto> pedidoProductos) {
        for (PedidoProducto pedidoProducto : pedidoProductos) {
            HistorialPedido historialPedido = new HistorialPedido();
            historialPedido.setPedido(pedidoProducto.getPedido());
            historialPedido.setUsuario(pedidoProducto.getPedido().getUsuario()); 
            historialPedido.setProductoId(pedidoProducto.getProducto().getId()); 
            historialPedido.setCantidad(pedidoProducto.getCantidad());
            historialPedido.setSubtotal(pedidoProducto.getSubtotal());
    
            // Guarda el historial en la base de datos
            historialPedidoRepositorio.save(historialPedido);
        }
    }
    

    public List<HistorialPedido> obtenerHistorialPorUsuario(Long usuarioId) {
        return historialPedidoRepositorio.findByUsuarioId(usuarioId);
    }

}
