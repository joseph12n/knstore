package com.mycompany.knstore.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Vista previa de totales de checkout sin persistir el pedido.
 */
public class CheckoutPreviewDTO implements Serializable {

    private BigDecimal subtotal;

    private BigDecimal iva;

    private BigDecimal envio;

    private BigDecimal total;

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    public BigDecimal getEnvio() {
        return envio;
    }

    public void setEnvio(BigDecimal envio) {
        this.envio = envio;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
