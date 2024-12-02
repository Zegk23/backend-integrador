package com.backend.integrador.Service;

import com.backend.integrador.Models.ConfirmacionPedido;
import com.backend.integrador.Models.Pedido;
import com.backend.integrador.Repository.ConfirmacionPedidoRepositorio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class ConfirmacionPedidoService {

    @Autowired
    private ConfirmacionPedidoRepositorio confirmacionPedidoRepositorio;

    public void registrarConfirmacion(Pedido pedido, String correoUsuario) {
        log.info("Iniciando registro de confirmación de pedido para el pedido ID: {} y correo: {}", 
                pedido.getId(), correoUsuario);
        try {
            ConfirmacionPedido confirmacion = new ConfirmacionPedido();
            confirmacion.setPedido(pedido);
            confirmacion.setCorreoUsuario(correoUsuario);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String fechaEnvio = LocalDateTime.now().format(formatter);
            confirmacion.setFechaEnvio(fechaEnvio);

            confirmacion.setEstado("Enviado");

            confirmacionPedidoRepositorio.save(confirmacion);

            log.info("Confirmación de pedido registrada exitosamente. Pedido ID: {}, Fecha de Envío: {}, Estado: {}",
                    pedido.getId(), fechaEnvio, "Enviado");
        } catch (Exception e) {
            log.error("Error al registrar la confirmación del pedido ID: {}. Detalle del error: {}",
                    pedido.getId(), e.getMessage());
        }
    }
}
