package com.backend.integrador.Service;

import com.backend.integrador.Models.Direccion;
import com.backend.integrador.Models.MetodoPago;
import com.backend.integrador.Models.Pago;
import com.backend.integrador.Models.Pedido;
import com.backend.integrador.Models.PedidoProducto;
import com.backend.integrador.Models.Producto;
import com.backend.integrador.Models.Usuario;
import com.backend.integrador.Repository.PagoRepositorio;
import com.backend.integrador.Repository.PedidoProductoRepositorio;
import com.backend.integrador.Repository.PedidoRepositorio;
import com.backend.integrador.Repository.ProductoRepositorio;
import com.backend.integrador.Repository.MetodoPagoRepositorio;
import com.backend.integrador.Repository.UsuarioRepositorio;
import com.backend.integrador.Repository.DireccionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private MetodoPagoRepositorio metodoPagoRepositorio;

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private PedidoProductoRepositorio pedidoProductoRepositorio;

    @Autowired
    private ProductoRepositorio productoRepositorio;

    @Autowired
    private PagoRepositorio pagoRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio; // Asegúrate de tener este repositorio inyectado

    @Autowired
    private DireccionRepositorio direaccionRepositorio;

    /**
     * Método para obtener un Método de Pago por su ID
     */
    public MetodoPago obtenerMetodoPagoPorId(Long id) {
        return metodoPagoRepositorio.findById(id).orElse(null);
    }

    /**
     * Método para obtener un pedido por su ID
     */
    public Pedido obtenerPedidoPorId(Long id) {
        return pedidoRepositorio.findById(id).orElse(null);
    }

    /**
     * Método para guardar un pago
     */
    public Pago guardarPago(Pago pago) {
        return pagoRepositorio.save(pago);
    }

    /**
     * Método para crear un pedido y asociar los productos
     */
    public Pedido crearPedido(Pedido pedido, List<PedidoProducto> productos) {
        // Verificar si el usuario existe
        Usuario usuario = usuarioRepositorio.findById(pedido.getUsuario().getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        pedido.setUsuario(usuario);

        // Guardar la dirección si no existe
        Direccion direccion = pedido.getDireccion();
        if (direccion != null && direccion.getId() == null) {
            direccion.setUsuario(usuario);
            direccion = direaccionRepositorio.save(direccion);
            pedido.setDireccion(direccion);
        }

        // Asignar productos al pedido
        for (PedidoProducto pedidoProducto : new ArrayList<>(productos)) { // Crea una copia de la lista para iterar
            Producto producto = productoRepositorio.findById(pedidoProducto.getProducto().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

            if (producto.getStock() < pedidoProducto.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            pedidoProducto.setSubtotal(producto.getPrecio() * pedidoProducto.getCantidad());
            producto.setStock(producto.getStock() - pedidoProducto.getCantidad());
            pedido.agregarPedidoProducto(pedidoProducto); // Modifica la lista original
            productoRepositorio.save(producto);
        }

        // Guardar el pedido completo junto con los productos
        return pedidoRepositorio.save(pedido);
    }

}
