package com.mycompany.knstore.service.dto;

import java.io.Serializable;

/**
 * Resultado de un checkout atómico exitoso.
 */
public class CheckoutResultDTO implements Serializable {

    private PedidoDTO pedido;

    public PedidoDTO getPedido() {
        return pedido;
    }

    public void setPedido(PedidoDTO pedido) {
        this.pedido = pedido;
    }
}
