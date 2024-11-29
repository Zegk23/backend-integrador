package com.backend.integrador.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.backend.integrador.Repository.HistorialPedidoRepositorio;
import com.backend.integrador.Models.HistorialPedido;
import com.backend.integrador.Models.Producto;
import com.backend.integrador.Repository.ProductoRepositorio; // Asegúrate de tener este repositorio para acceder a los productos

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
    private ProductoRepositorio productoRepositorio; // Importa el repositorio de Producto

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Map<String, Object>>> obtenerHistorialPorUsuario(@PathVariable Long idUsuario) {
        List<HistorialPedido> historial = historialPedidoRepositorio.findByUsuarioId(idUsuario);

        // Agrupar los productos por 'pedidoId'
        Map<Long, List<HistorialPedido>> agrupadoPorPedido = historial.stream()
            .collect(Collectors.groupingBy(pedido -> pedido.getPedido().getId()));

        // Crear la respuesta con el historial agrupado
        List<Map<String, Object>> response = agrupadoPorPedido.entrySet().stream()
            .map(entry -> {
                long pedidoId = entry.getKey();
                List<HistorialPedido> productos = entry.getValue();

                // Calcular el total del pedido
                double totalPedido = productos.stream()
                    .mapToDouble(HistorialPedido::getSubtotal)
                    .sum();

                // Crear la respuesta por pedido
                Map<String, Object> pedidoResponse = Map.of(
                    "pedidoId", pedidoId,
                    "total", totalPedido,
                    "productos", productos.stream().map(pedido -> {
                        Producto producto = productoRepositorio.findById(pedido.getProductoId()).orElse(null); // Obtener el Producto por ID
                        return Map.of(
                            "productoId", pedido.getProductoId(),
                            "nombreProducto", producto != null ? producto.getNombre() : "Desconocido", // Asignar nombre
                            "imageUrl", producto != null ? producto.getImageUrl() : "", // Asignar URL de la imagen
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
