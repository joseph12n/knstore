package com.mycompany.knstore.service.dto;

import java.io.Serializable;

/**
 * Resultado de un checkout atómico exitoso.
 */
public class CheckoutResultDTO implements Serializable {

    private PedidoDTO pedido;

    /**
     * Pago aprobado en la misma transacción del checkout (RF-076): la pasarela
     * simbólica deja el pago en APPROVED y el cliente lo recibe ya resuelto.
     */
    private PagoDTO pago;

    public PedidoDTO getPedido() {
        return pedido;
    }

    public void setPedido(PedidoDTO pedido) {
        this.pedido = pedido;
    }

    public PagoDTO getPago() {
        return pago;
    }

    public void setPago(PagoDTO pago) {
        this.pago = pago;
    }
}
