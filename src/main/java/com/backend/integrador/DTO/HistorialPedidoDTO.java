package com.backend.integrador.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HistorialPedidoDTO {
    private Long id;
    private Long productoId;
    private String nombreProducto;
    private String imageUrl; // URL de la imagen del producto
    private int cantidad;
    private double subtotal;
    private String fecha;
}
