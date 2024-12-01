package com.backend.integrador.Controller;

import com.backend.integrador.Models.HistorialPedido;
import com.backend.integrador.Models.Producto;
import com.backend.integrador.Service.HistorialPedidoService;
import com.backend.integrador.Repository.HistorialPedidoRepositorio;
import com.backend.integrador.Repository.ProductoRepositorio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/historial-pedidos")
public class HistorialPedidoController {

    @Autowired
    private HistorialPedidoRepositorio historialPedidoRepositorio;

    @Autowired
    private ProductoRepositorio productoRepositorio;

    @Autowired
    private HistorialPedidoService historialPedidoService;

    // Obtener historial de pedidos por usuario
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

                    // Obtener el pedido desde la relación de `HistorialPedido`
                    var pedido = productos.get(0).getPedido();

                    Map<String, Object> pedidoResponse = Map.of(
                            "pedidoId", pedidoId,
                            "total", totalPedido,
                            "estado", pedido.getEstado(),
                            "productos", productos.stream().map(pedidoProducto -> {
                                Producto producto = productoRepositorio.findById(pedidoProducto.getProductoId())
                                        .orElse(null);
                                return Map.of(
                                        "productoId", pedidoProducto.getProductoId(),
                                        "nombreProducto",
                                        producto != null ? producto.getNombre() : "Desconocido",
                                        "imageUrl", producto != null ? producto.getImageUrl() : "",
                                        "cantidad", pedidoProducto.getCantidad(),
                                        "subtotal", pedidoProducto.getSubtotal());
                            }).collect(Collectors.toList()));
                    return pedidoResponse;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // Obtener detalles de un pedido específico
    @GetMapping("/detalle/{pedidoId}")
    public ResponseEntity<Map<String, Object>> obtenerDetallePedido(@PathVariable Long pedidoId) {
        Map<String, Object> detallePedido = historialPedidoService.obtenerDetallePedido(pedidoId);

        if (detallePedido == null) {
            return ResponseEntity.notFound().build(); // Devuelve 404 si no se encuentra el pedido
        }

        return ResponseEntity.ok(detallePedido);
    }
}
