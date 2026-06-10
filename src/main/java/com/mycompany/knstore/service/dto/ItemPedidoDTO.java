package com.mycompany.knstore.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.knstore.domain.ItemPedido} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ItemPedidoDTO implements Serializable {

    private String id;

    @NotNull
    @Min(value = 1)
    private Integer cantidad;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal precioUnitario;

    @DecimalMin(value = "0")
    private BigDecimal porcentajeIva;

    @DecimalMin(value = "0")
    private BigDecimal valorIva;

    @DecimalMin(value = "0")
    private BigDecimal descuento;

    private BigDecimal subtotal;

    @NotNull
    private PedidoDTO pedido;

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

    public BigDecimal getPorcentajeIva() {
        return porcentajeIva;
    }

    public void setPorcentajeIva(BigDecimal porcentajeIva) {
        this.porcentajeIva = porcentajeIva;
    }

    public BigDecimal getValorIva() {
        return valorIva;
    }

    public void setValorIva(BigDecimal valorIva) {
        this.valorIva = valorIva;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public PedidoDTO getPedido() {
        return pedido;
    }

    public void setPedido(PedidoDTO pedido) {
        this.pedido = pedido;
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
        if (!(o instanceof ItemPedidoDTO)) {
            return false;
        }

        ItemPedidoDTO itemPedidoDTO = (ItemPedidoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, itemPedidoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ItemPedidoDTO{" +
            "id='" + getId() + "'" +
            ", cantidad=" + getCantidad() +
            ", precioUnitario=" + getPrecioUnitario() +
            ", porcentajeIva=" + getPorcentajeIva() +
            ", valorIva=" + getValorIva() +
            ", descuento=" + getDescuento() +
            ", subtotal=" + getSubtotal() +
            ", pedido=" + getPedido() +
            ", producto=" + getProducto() +
            ", variante=" + getVariante() +
            "}";
    }
}
