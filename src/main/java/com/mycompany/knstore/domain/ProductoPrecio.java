package com.mycompany.knstore.domain;

import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A ProductoPrecio.
 */
@Document(collection = "producto_precio")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductoPrecio implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @DecimalMin(value = "0")
    @Field("precio_compra")
    private BigDecimal precioCompra;

    @NotNull
    @DecimalMin(value = "0")
    @Field("precio_venta")
    private BigDecimal precioVenta;

    @DecimalMin(value = "0")
    @Field("precio_adicional")
    private BigDecimal precioAdicional;

    @Field("ganancia")
    private BigDecimal ganancia;

    @DBRef
    private Producto producto;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public ProductoPrecio id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getPrecioCompra() {
        return this.precioCompra;
    }

    public ProductoPrecio precioCompra(BigDecimal precioCompra) {
        this.setPrecioCompra(precioCompra);
        return this;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public BigDecimal getPrecioVenta() {
        return this.precioVenta;
    }

    public ProductoPrecio precioVenta(BigDecimal precioVenta) {
        this.setPrecioVenta(precioVenta);
        return this;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public BigDecimal getPrecioAdicional() {
        return this.precioAdicional;
    }

    public ProductoPrecio precioAdicional(BigDecimal precioAdicional) {
        this.setPrecioAdicional(precioAdicional);
        return this;
    }

    public void setPrecioAdicional(BigDecimal precioAdicional) {
        this.precioAdicional = precioAdicional;
    }

    public BigDecimal getGanancia() {
        return this.ganancia;
    }

    public ProductoPrecio ganancia(BigDecimal ganancia) {
        this.setGanancia(ganancia);
        return this;
    }

    public void setGanancia(BigDecimal ganancia) {
        this.ganancia = ganancia;
    }

    public Producto getProducto() {
        return this.producto;
    }

    public void setProducto(Producto producto) {
        if (this.producto != null) {
            this.producto.setPrecio(null);
        }
        if (producto != null) {
            producto.setPrecio(this);
        }
        this.producto = producto;
    }

    public ProductoPrecio producto(Producto producto) {
        this.setProducto(producto);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductoPrecio)) {
            return false;
        }
        return getId() != null && getId().equals(((ProductoPrecio) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductoPrecio{" +
            "id=" + getId() +
            ", precioCompra=" + getPrecioCompra() +
            ", precioVenta=" + getPrecioVenta() +
            ", precioAdicional=" + getPrecioAdicional() +
            ", ganancia=" + getGanancia() +
            "}";
    }
}
