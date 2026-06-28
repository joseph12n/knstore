package com.mycompany.knstore.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO returned after a successful atomic checkout.
 */
public class CheckoutResultDTO implements Serializable {

    private String pedidoId;

    private String numeroPedido;

    private BigDecimal total;

    private String estado;

    public CheckoutResultDTO(String pedidoId, String numeroPedido, BigDecimal total, String estado) {
        this.pedidoId = pedidoId;
        this.numeroPedido = numeroPedido;
        this.total = total;
        this.estado = estado;
    }

    public String getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(String pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(String numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
