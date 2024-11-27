package com.backend.integrador.DTO;

import com.backend.integrador.Models.Pedido;
import com.backend.integrador.Models.PedidoProducto;
import com.backend.integrador.Models.RecojoTienda;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PedidoRequest {
    private Pedido pedido;
    private List<PedidoProducto> productos;
    private RecojoTienda recojoTienda;
}
