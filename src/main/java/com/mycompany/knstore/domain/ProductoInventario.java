package com.mycompany.knstore.domain;

import com.mycompany.knstore.domain.enumeration.UbicacionBodega;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A ProductoInventario.
 */
@Document(collection = "producto_inventario")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductoInventario implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Min(value = 0)
    @Field("stock")
    private Integer stock;

    @Min(value = 0)
    @Field("stock_minimo")
    private Integer stockMinimo;

    @Field("ubicacion_bodega")
    private UbicacionBodega ubicacionBodega;

    @Min(value = 0)
    @Field("garantia_meses")
    private Integer garantiaMeses;

    @DBRef
    private Producto producto;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public ProductoInventario id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getStock() {
        return this.stock;
    }

    public ProductoInventario stock(Integer stock) {
        this.setStock(stock);
        return this;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStockMinimo() {
        return this.stockMinimo;
    }

    public ProductoInventario stockMinimo(Integer stockMinimo) {
        this.setStockMinimo(stockMinimo);
        return this;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public UbicacionBodega getUbicacionBodega() {
        return this.ubicacionBodega;
    }

    public ProductoInventario ubicacionBodega(UbicacionBodega ubicacionBodega) {
        this.setUbicacionBodega(ubicacionBodega);
        return this;
    }

    public void setUbicacionBodega(UbicacionBodega ubicacionBodega) {
        this.ubicacionBodega = ubicacionBodega;
    }

    public Integer getGarantiaMeses() {
        return this.garantiaMeses;
    }

    public ProductoInventario garantiaMeses(Integer garantiaMeses) {
        this.setGarantiaMeses(garantiaMeses);
        return this;
    }

    public void setGarantiaMeses(Integer garantiaMeses) {
        this.garantiaMeses = garantiaMeses;
    }

    public Producto getProducto() {
        return this.producto;
    }

    public void setProducto(Producto producto) {
        if (this.producto != null) {
            this.producto.setInventario(null);
        }
        if (producto != null) {
            producto.setInventario(this);
        }
        this.producto = producto;
    }

    public ProductoInventario producto(Producto producto) {
        this.setProducto(producto);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductoInventario)) {
            return false;
        }
        return getId() != null && getId().equals(((ProductoInventario) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductoInventario{" +
            "id=" + getId() +
            ", stock=" + getStock() +
            ", stockMinimo=" + getStockMinimo() +
            ", ubicacionBodega='" + getUbicacionBodega() + "'" +
            ", garantiaMeses=" + getGarantiaMeses() +
            "}";
    }
}
