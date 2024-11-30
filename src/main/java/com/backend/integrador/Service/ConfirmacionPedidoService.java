package com.backend.integrador.Service;

import com.backend.integrador.Models.ConfirmacionPedido;
import com.backend.integrador.Models.Pedido;
import com.backend.integrador.Repository.ConfirmacionPedidoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ConfirmacionPedidoService {

    @Autowired
    private ConfirmacionPedidoRepositorio confirmacionPedidoRepositorio;


    public void registrarConfirmacion(Pedido pedido, String correoUsuario) {
        ConfirmacionPedido confirmacion = new ConfirmacionPedido();
        confirmacion.setPedido(pedido);
        confirmacion.setCorreoUsuario(correoUsuario);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fechaEnvio = LocalDateTime.now().format(formatter);
        confirmacion.setFechaEnvio(fechaEnvio);

        confirmacion.setEstado("Enviado");

        confirmacionPedidoRepositorio.save(confirmacion);
    }
}
