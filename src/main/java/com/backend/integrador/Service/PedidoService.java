package com.backend.integrador.Service;

import com.backend.integrador.Exceptions.StockInsuficienteException;
import com.backend.integrador.Models.*;
import com.backend.integrador.Repository.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
public class PedidoService {

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private ConfirmacionPedidoService confirmacionPedidoService;

    @Autowired
    private PedidoProductoRepositorio pedidoProductoRepositorio;

    @Autowired
    private ProductoRepositorio productoRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private DireccionRepositorio direccionRepositorio;

    @Autowired
    private RecojoTiendaRepositorio recojoTiendaRepositorio;

    @Autowired
    private PagoService pagoService;

    @Autowired
    private HistorialPedidoService historialPedidoService;

    @Autowired
    private CorreosService correosService; // Servicio para enviar correos

    @Transactional
    public Pedido crearPedido(Pedido pedido, List<PedidoProducto> productos, RecojoTienda recojoTienda,
                              String stripePaymentId, String metodoPagoNombre) {
        log.info("Iniciando creación del pedido...");

        validarParametros(stripePaymentId, metodoPagoNombre);

        Usuario usuario = validarUsuario(pedido.getUsuario().getId());
        pedido.setUsuario(usuario);

        if (pedido.getDireccion() == null && recojoTienda != null) {
            manejarRecojoEnTienda(pedido, recojoTienda);
        } else if (pedido.getDireccion() != null) {
            manejarDelivery(pedido, usuario);
        }

        Pedido pedidoGuardado = pedidoRepositorio.save(pedido);

        manejarProductos(pedidoGuardado, productos);

        registrarPago(pedidoGuardado, stripePaymentId, metodoPagoNombre);

        historialPedidoService.registrarHistorialPedido(productos);

        // Enviar correo de confirmación
        enviarCorreoConfirmacion(pedidoGuardado, usuario);

        log.info("Pedido creado exitosamente. ID: {}", pedidoGuardado.getId());
        return pedidoGuardado;
    }

    private void validarParametros(String stripePaymentId, String metodoPagoNombre) {
        if (stripePaymentId == null || stripePaymentId.isEmpty()) {
            throw new IllegalArgumentException("El Stripe Payment ID no puede estar vacío.");
        }
        if (metodoPagoNombre == null || metodoPagoNombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre del método de pago no puede estar vacío.");
        }
    }

    private Usuario validarUsuario(Long usuarioId) {
        return usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    private void manejarRecojoEnTienda(Pedido pedido, RecojoTienda recojoTienda) {
        Direccion direccionTienda = direccionRepositorio.findByDireccionIgnoreCase(recojoTienda.getLocal())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Dirección para el local no encontrada: " + recojoTienda.getLocal()));

        pedido.setDireccion(direccionTienda);

        Pedido pedidoGuardado = pedidoRepositorio.save(pedido);

        recojoTienda.setPedido(pedidoGuardado);
        recojoTiendaRepositorio.save(recojoTienda);
    }

    private void manejarDelivery(Pedido pedido, Usuario usuario) {
        Direccion direccionPedido = direccionRepositorio.findByUsuarioAndDireccionAndCiudadAndCodigoPostalAndPais(
                usuario,
                pedido.getDireccion().getDireccion(),
                pedido.getDireccion().getCiudad(),
                pedido.getDireccion().getCodigoPostal(),
                pedido.getDireccion().getPais())
                .orElseGet(() -> {
                    pedido.getDireccion().setUsuario(usuario);
                    return direccionRepositorio.save(pedido.getDireccion());
                });

        pedido.setDireccion(direccionPedido);
    }

    private void manejarProductos(Pedido pedido, List<PedidoProducto> productos) {
        for (PedidoProducto pedidoProducto : productos) {
            Producto producto = productoRepositorio.findById(pedidoProducto.getProducto().getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Producto no encontrado: " + pedidoProducto.getProducto().getId()));

            if (producto.getStock() < pedidoProducto.getCantidad()) {
                throw new StockInsuficienteException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            producto.setStock(producto.getStock() - pedidoProducto.getCantidad());
            productoRepositorio.save(producto);

            pedidoProducto.setSubtotal(producto.getPrecio() * pedidoProducto.getCantidad());
            pedidoProducto.setProducto(producto);
            pedidoProducto.setPedido(pedido);
        }

        pedidoProductoRepositorio.saveAll(productos);
    }

    private void registrarPago(Pedido pedido, String stripePaymentId, String metodoPagoNombre) {
        double montoTotal = pedidoProductoRepositorio.findByPedido(pedido).stream()
                .mapToDouble(PedidoProducto::getSubtotal)
                .sum();

        log.info("Registrando pago con monto total: {}", montoTotal);

        pagoService.registrarPago(pedido, montoTotal, pedido.getFecha(), stripePaymentId, metodoPagoNombre);
    }

    private void enviarCorreoConfirmacion(Pedido pedido, Usuario usuario) {
        try {
            correosService.sendEmail(
                    usuario.getCorreo(), // Cambia getEmail() a getCorreo() para que coincida con tu modelo
                    "Confirmación de Pedido - Velazco Panadería y Dulcería",
                    "confirmacionPedidoTemplate", // Nombre del template Thymeleaf
                    usuario.getNombre()
            );
    
            log.info("Correo de confirmación enviado exitosamente al usuario: {}", usuario.getCorreo());
    
            // Registrar la confirmación del pedido en la base de datos
            confirmacionPedidoService.registrarConfirmacion(pedido, usuario.getCorreo());
    
        } catch (Exception e) {
            log.error("Error al enviar el correo de confirmación: {}", e.getMessage(), e);
        }
    }
    
    
}
