package com.mycompany.knstore.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.knstore.domain.ItemCarrito} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ItemCarritoDTO implements Serializable {

    private String id;

    @NotNull
    @Min(value = 1)
    private Integer cantidad;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal precioUnitario;

    private BigDecimal subtotal;

    @NotNull
    private CarritoDTO carrito;

    @NotNull
    private ProductoDTO producto;

    private VarianteProductoDTO variante;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public CarritoDTO getCarrito() {
        return carrito;
    }

    public void setCarrito(CarritoDTO carrito) {
        this.carrito = carrito;
    }

    public ProductoDTO getProducto() {
        return producto;
    }

    public void setProducto(ProductoDTO producto) {
        this.producto = producto;
    }

    public VarianteProductoDTO getVariante() {
        return variante;
    }

    public void setVariante(VarianteProductoDTO variante) {
        this.variante = variante;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemCarritoDTO)) {
            return false;
        }

        ItemCarritoDTO itemCarritoDTO = (ItemCarritoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, itemCarritoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ItemCarritoDTO{" +
            "id='" + getId() + "'" +
            ", cantidad=" + getCantidad() +
            ", precioUnitario=" + getPrecioUnitario() +
            ", subtotal=" + getSubtotal() +
            ", carrito=" + getCarrito() +
            ", producto=" + getProducto() +
            ", variante=" + getVariante() +
            "}";
    }
}
