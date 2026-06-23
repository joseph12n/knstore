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
    @Size(max = 200)
    private String nombreProducto;

    @Size(max = 220)
    private String slugProducto;

    @Size(max = 100)
    private String marcaProducto;

    @Size(max = 100)
    private String skuProducto;

    @Size(max = 50)
    private String colorProducto;

    @Size(max = 30)
    private String tallaProducto;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getSlugProducto() {
        return slugProducto;
    }

    public void setSlugProducto(String slugProducto) {
        this.slugProducto = slugProducto;
    }

    public String getMarcaProducto() {
        return marcaProducto;
    }

    public void setMarcaProducto(String marcaProducto) {
        this.marcaProducto = marcaProducto;
    }

    public String getSkuProducto() {
        return skuProducto;
    }

    public void setSkuProducto(String skuProducto) {
        this.skuProducto = skuProducto;
    }

    public String getColorProducto() {
        return colorProducto;
    }

    public void setColorProducto(String colorProducto) {
        this.colorProducto = colorProducto;
    }

    public String getTallaProducto() {
        return tallaProducto;
    }

    public void setTallaProducto(String tallaProducto) {
        this.tallaProducto = tallaProducto;
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
            ", nombreProducto='" + getNombreProducto() + "'" +
            ", slugProducto='" + getSlugProducto() + "'" +
            ", marcaProducto='" + getMarcaProducto() + "'" +
            ", skuProducto='" + getSkuProducto() + "'" +
            ", colorProducto='" + getColorProducto() + "'" +
            ", tallaProducto='" + getTallaProducto() + "'" +
            ", cantidad=" + getCantidad() +
            ", precioUnitario=" + getPrecioUnitario() +
            ", porcentajeIva=" + getPorcentajeIva() +
            ", valorIva=" + getValorIva() +
            ", descuento=" + getDescuento() +
            ", subtotal=" + getSubtotal() +
            ", pedido=" + getPedido() +
            ", producto=" + getProducto() +
            "}";
    }
}
