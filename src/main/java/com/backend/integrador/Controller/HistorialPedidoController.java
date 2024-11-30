package com.backend.integrador.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.backend.integrador.Repository.HistorialPedidoRepositorio;
import com.backend.integrador.Models.HistorialPedido;
import com.backend.integrador.Models.Producto;
import com.backend.integrador.Repository.ProductoRepositorio; 

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/historial-pedidos")
@CrossOrigin(origins = "http://localhost:3000")
public class HistorialPedidoController {

    @Autowired
    private HistorialPedidoRepositorio historialPedidoRepositorio;

    @Autowired
    private ProductoRepositorio productoRepositorio; 

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Map<String, Object>>> obtenerHistorialPorUsuario(@PathVariable Long idUsuario) {
        List<HistorialPedido> historial = historialPedidoRepositorio.findByUsuarioId(idUsuario);

        Map<Long, List<HistorialPedido>> agrupadoPorPedido = historial.stream()
            .collect(Collectors.groupingBy(pedido -> pedido.getPedido().getId()));

        List<Map<String, Object>> response = agrupadoPorPedido.entrySet().stream()
            .map(entry -> {
                long pedidoId = entry.getKey();
                List<HistorialPedido> productos = entry.getValue();

                double totalPedido = productos.stream()
                    .mapToDouble(HistorialPedido::getSubtotal)
                    .sum();

                Map<String, Object> pedidoResponse = Map.of(
                    "pedidoId", pedidoId,
                    "total", totalPedido,
                    "productos", productos.stream().map(pedido -> {
                        Producto producto = productoRepositorio.findById(pedido.getProductoId()).orElse(null); 
                        return Map.of(
                            "productoId", pedido.getProductoId(),
                            "nombreProducto", producto != null ? producto.getNombre() : "Desconocido", 
                            "imageUrl", producto != null ? producto.getImageUrl() : "", 
                            "cantidad", pedido.getCantidad(),
                            "subtotal", pedido.getSubtotal()
                        );
                    }).collect(Collectors.toList())
                );
                return pedidoResponse;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
