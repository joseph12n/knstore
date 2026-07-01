package com.mycompany.knstore.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Item de carrito enviado en una solicitud de checkout.
 */
public class CheckoutItemDTO implements Serializable {

    @NotBlank
    private String productoId;

    @NotNull
    @Min(value = 1)
    private Integer cantidad;

    @NotNull
    @Min(value = 0)
    private BigDecimal precioUnitario;

    public String getProductoId() {
        return productoId;
    }

    public void setProductoId(String productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}
