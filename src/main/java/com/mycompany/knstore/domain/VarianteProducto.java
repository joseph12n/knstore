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
 * A VarianteProducto.
 */
@Document(collection = "variante_producto")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VarianteProducto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Size(max = 100)
    @Field("sku")
    private String sku;

    @Size(max = 50)
    @Field("color")
    private String color;

    @Size(max = 30)
    @Field("talla")
    private String talla;

    @Size(max = 50)
    @Field("codigo_barras")
    private String codigoBarras;

    @NotNull
    @Min(value = 0)
    @Field("stock")
    private Integer stock;

    @Min(value = 0)
    @Field("stock_minimo")
    private Integer stockMinimo;

    @Size(max = 80)
    @Field("ubicacion_bodega")
    private String ubicacionBodega;

    @DecimalMin(value = "0")
    @Field("precio_adicional")
    private BigDecimal precioAdicional;

    @NotNull
    @Field("activo")
    private Boolean activo;

    @DBRef
    @Field("producto")
    @JsonIgnoreProperties(value = { "subcategoria" }, allowSetters = true)
    private Producto producto;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public VarianteProducto id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSku() {
        return this.sku;
    }

    public VarianteProducto sku(String sku) {
        this.setSku(sku);
        return this;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getColor() {
        return this.color;
    }

    public VarianteProducto color(String color) {
        this.setColor(color);
        return this;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTalla() {
        return this.talla;
    }

    public VarianteProducto talla(String talla) {
        this.setTalla(talla);
        return this;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getCodigoBarras() {
        return this.codigoBarras;
    }

    public VarianteProducto codigoBarras(String codigoBarras) {
        this.setCodigoBarras(codigoBarras);
        return this;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public Integer getStock() {
        return this.stock;
    }

    public VarianteProducto stock(Integer stock) {
        this.setStock(stock);
        return this;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStockMinimo() {
        return this.stockMinimo;
    }

    public VarianteProducto stockMinimo(Integer stockMinimo) {
        this.setStockMinimo(stockMinimo);
        return this;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public String getUbicacionBodega() {
        return this.ubicacionBodega;
    }

    public VarianteProducto ubicacionBodega(String ubicacionBodega) {
        this.setUbicacionBodega(ubicacionBodega);
        return this;
    }

    public void setUbicacionBodega(String ubicacionBodega) {
        this.ubicacionBodega = ubicacionBodega;
    }

    public BigDecimal getPrecioAdicional() {
        return this.precioAdicional;
    }

    public VarianteProducto precioAdicional(BigDecimal precioAdicional) {
        this.setPrecioAdicional(precioAdicional);
        return this;
    }

    public void setPrecioAdicional(BigDecimal precioAdicional) {
        this.precioAdicional = precioAdicional;
    }

    public Boolean getActivo() {
        return this.activo;
    }

    public VarianteProducto activo(Boolean activo) {
        this.setActivo(activo);
        return this;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Producto getProducto() {
        return this.producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public VarianteProducto producto(Producto producto) {
        this.setProducto(producto);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VarianteProducto)) {
            return false;
        }
        return getId() != null && getId().equals(((VarianteProducto) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VarianteProducto{" +
            "id=" + getId() +
            ", sku='" + getSku() + "'" +
            ", color='" + getColor() + "'" +
            ", talla='" + getTalla() + "'" +
            ", codigoBarras='" + getCodigoBarras() + "'" +
            ", stock=" + getStock() +
            ", stockMinimo=" + getStockMinimo() +
            ", ubicacionBodega='" + getUbicacionBodega() + "'" +
            ", precioAdicional=" + getPrecioAdicional() +
            ", activo='" + getActivo() + "'" +
            "}";
    }
}
