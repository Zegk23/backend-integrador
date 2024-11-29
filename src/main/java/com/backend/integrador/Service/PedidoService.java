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
    private HistorialPedidoService historialPedidoService; // Agregado

    @Transactional
    public Pedido crearPedido(Pedido pedido, List<PedidoProducto> productos, RecojoTienda recojoTienda,
                              String stripePaymentId, String metodoPagoNombre) {
        log.info("Iniciando creación del pedido...");

        validarParametros(stripePaymentId, metodoPagoNombre);

        // Validar usuario
        Usuario usuario = usuarioRepositorio.findById(pedido.getUsuario().getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        pedido.setUsuario(usuario);

        // Manejo de dirección o recojo en tienda
        if (pedido.getDireccion() == null && recojoTienda != null) {
            manejarRecojoEnTienda(pedido, recojoTienda);
        } else if (pedido.getDireccion() != null) {
            manejarDelivery(pedido, usuario);
        }

        // Guardar pedido
        Pedido pedidoGuardado = pedidoRepositorio.save(pedido);

        // Manejo de productos
        manejarProductos(pedidoGuardado, productos);

        // Registrar pago
        registrarPago(pedidoGuardado, stripePaymentId, metodoPagoNombre);

        // Registrar historial del pedido
        historialPedidoService.registrarHistorialPedido(productos); // Agregado

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

            // Actualiza el stock del producto
            producto.setStock(producto.getStock() - pedidoProducto.getCantidad());
            productoRepositorio.save(producto);

            // Calcula y asigna el subtotal
            pedidoProducto.setSubtotal(producto.getPrecio() * pedidoProducto.getCantidad());
            pedidoProducto.setProducto(producto);
            pedidoProducto.setPedido(pedido);
        }
        // Guarda todos los productos relacionados con el pedido
        pedidoProductoRepositorio.saveAll(productos);
    }

    private void registrarPago(Pedido pedido, String stripePaymentId, String metodoPagoNombre) {
        // Asegúrate de que los subtotales están correctamente calculados
        double montoTotal = pedidoProductoRepositorio.findByPedido(pedido).stream()
                .mapToDouble(PedidoProducto::getSubtotal)
                .sum();

        log.info("Delegando registro de pago al PagoService con monto total: {}", montoTotal);

        // Llamada al PagoService para registrar el pago
        pagoService.registrarPago(pedido, montoTotal, pedido.getFecha(), stripePaymentId, metodoPagoNombre);
    }
}
