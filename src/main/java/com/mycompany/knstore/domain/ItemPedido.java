package com.mycompany.knstore.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A ItemPedido.
 */
@Document(collection = "item_pedido")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ItemPedido implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Min(value = 1)
    @Field("cantidad")
    private Integer cantidad;

    @NotNull
    @DecimalMin(value = "0")
    @Field("precio_unitario")
    private BigDecimal precioUnitario;

    @DecimalMin(value = "0")
    @Field("porcentaje_iva")
    private BigDecimal porcentajeIva;

    @DecimalMin(value = "0")
    @Field("valor_iva")
    private BigDecimal valorIva;

    @DecimalMin(value = "0")
    @Field("descuento")
    private BigDecimal descuento;

    @Field("subtotal")
    private BigDecimal subtotal;

    @DBRef
    @Field("pedido")
    @JsonIgnoreProperties(value = { "direccion", "envio", "cuenta" }, allowSetters = true)
    private Pedido pedido;

    @DBRef
    @Field("producto")
    @JsonIgnoreProperties(value = { "subcategoria" }, allowSetters = true)
    private Producto producto;

    @DBRef
    @Field("variante")
    @JsonIgnoreProperties(value = { "producto" }, allowSetters = true)
    private VarianteProducto variante;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public ItemPedido id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCantidad() {
        return this.cantidad;
    }

    public ItemPedido cantidad(Integer cantidad) {
        this.setCantidad(cantidad);
        return this;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return this.precioUnitario;
    }

    public ItemPedido precioUnitario(BigDecimal precioUnitario) {
        this.setPrecioUnitario(precioUnitario);
        return this;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getPorcentajeIva() {
        return this.porcentajeIva;
    }

    public ItemPedido porcentajeIva(BigDecimal porcentajeIva) {
        this.setPorcentajeIva(porcentajeIva);
        return this;
    }

    public void setPorcentajeIva(BigDecimal porcentajeIva) {
        this.porcentajeIva = porcentajeIva;
    }

    public BigDecimal getValorIva() {
        return this.valorIva;
    }

    public ItemPedido valorIva(BigDecimal valorIva) {
        this.setValorIva(valorIva);
        return this;
    }

    public void setValorIva(BigDecimal valorIva) {
        this.valorIva = valorIva;
    }

    public BigDecimal getDescuento() {
        return this.descuento;
    }

    public ItemPedido descuento(BigDecimal descuento) {
        this.setDescuento(descuento);
        return this;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getSubtotal() {
        return this.subtotal;
    }

    public ItemPedido subtotal(BigDecimal subtotal) {
        this.setSubtotal(subtotal);
        return this;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Pedido getPedido() {
        return this.pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public ItemPedido pedido(Pedido pedido) {
        this.setPedido(pedido);
        return this;
    }

    public Producto getProducto() {
        return this.producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public ItemPedido producto(Producto producto) {
        this.setProducto(producto);
        return this;
    }

    public VarianteProducto getVariante() {
        return this.variante;
    }

    public void setVariante(VarianteProducto varianteProducto) {
        this.variante = varianteProducto;
    }

    public ItemPedido variante(VarianteProducto varianteProducto) {
        this.setVariante(varianteProducto);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemPedido)) {
            return false;
        }
        return getId() != null && getId().equals(((ItemPedido) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ItemPedido{" +
            "id=" + getId() +
            ", cantidad=" + getCantidad() +
            ", precioUnitario=" + getPrecioUnitario() +
            ", porcentajeIva=" + getPorcentajeIva() +
            ", valorIva=" + getValorIva() +
            ", descuento=" + getDescuento() +
            ", subtotal=" + getSubtotal() +
            "}";
    }
}
