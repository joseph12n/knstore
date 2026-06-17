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
 * A ItemCarrito.
 */
@Document(collection = "item_carrito")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ItemCarrito implements Serializable {

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

    @Field("subtotal")
    private BigDecimal subtotal;

    @DBRef
    @Field("carrito")
    @JsonIgnoreProperties(value = { "cuenta" }, allowSetters = true)
    private Carrito carrito;

    @DBRef
    @Field("producto")
    @JsonIgnoreProperties(
        value = { "precio", "inventario", "imageneses", "categoria", "subcategoria", "marca", "categoriaIva" },
        allowSetters = true
    )
    private Producto producto;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public ItemCarrito id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCantidad() {
        return this.cantidad;
    }

    public ItemCarrito cantidad(Integer cantidad) {
        this.setCantidad(cantidad);
        return this;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return this.precioUnitario;
    }

    public ItemCarrito precioUnitario(BigDecimal precioUnitario) {
        this.setPrecioUnitario(precioUnitario);
        return this;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return this.subtotal;
    }

    public ItemCarrito subtotal(BigDecimal subtotal) {
        this.setSubtotal(subtotal);
        return this;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Carrito getCarrito() {
        return this.carrito;
    }

    public void setCarrito(Carrito carrito) {
        this.carrito = carrito;
    }

    public ItemCarrito carrito(Carrito carrito) {
        this.setCarrito(carrito);
        return this;
    }

    public Producto getProducto() {
        return this.producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public ItemCarrito producto(Producto producto) {
        this.setProducto(producto);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemCarrito)) {
            return false;
        }
        return getId() != null && getId().equals(((ItemCarrito) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ItemCarrito{" +
            "id=" + getId() +
            ", cantidad=" + getCantidad() +
            ", precioUnitario=" + getPrecioUnitario() +
            ", subtotal=" + getSubtotal() +
            "}";
    }
}
