package com.mycompany.knstore.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Item de carrito enviado en una solicitud de checkout.
 *
 * <p>El precio unitario no se acepta del cliente: el servidor lo resuelve
 * siempre desde el producto en base de datos.</p>
 */
public class CheckoutItemDTO implements Serializable {

    @NotBlank
    private String productoId;

    @NotNull
    @Min(value = 1)
    private Integer cantidad;

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
}
