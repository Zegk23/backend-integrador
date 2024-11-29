package com.backend.integrador.Controller;

import com.backend.integrador.DTO.HistorialPedidoDTO;
import com.backend.integrador.Repository.HistorialPedidoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historial-pedidos")
@CrossOrigin(origins = "http://localhost:3000")
public class HistorialPedidoController {

    @Autowired
    private HistorialPedidoRepositorio historialPedidoRepositorio;

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<HistorialPedidoDTO>> obtenerHistorialPorUsuario(@PathVariable Long idUsuario) {
        List<HistorialPedidoDTO> historial = historialPedidoRepositorio.findHistorialWithProductDetailsByUsuarioId(idUsuario);
        return ResponseEntity.ok(historial);
    }
}
