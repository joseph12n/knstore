package com.mycompany.knstore.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.knstore.domain.VarianteProducto} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VarianteProductoDTO implements Serializable {

    private String id;

    @Size(max = 100)
    private String sku;

    @Size(max = 50)
    private String color;

    @Size(max = 30)
    private String talla;

    @Size(max = 50)
    private String codigoBarras;

    @NotNull
    @Min(value = 0)
    private Integer stock;

    @Min(value = 0)
    private Integer stockMinimo;

    @Size(max = 80)
    private String ubicacionBodega;

    @DecimalMin(value = "0")
    private BigDecimal precioAdicional;

    @NotNull
    private Boolean activo;

    @NotNull
    private ProductoDTO producto;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public String getUbicacionBodega() {
        return ubicacionBodega;
    }

    public void setUbicacionBodega(String ubicacionBodega) {
        this.ubicacionBodega = ubicacionBodega;
    }

    public BigDecimal getPrecioAdicional() {
        return precioAdicional;
    }

    public void setPrecioAdicional(BigDecimal precioAdicional) {
        this.precioAdicional = precioAdicional;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
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
        if (!(o instanceof VarianteProductoDTO)) {
            return false;
        }

        VarianteProductoDTO varianteProductoDTO = (VarianteProductoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, varianteProductoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VarianteProductoDTO{" +
            "id='" + getId() + "'" +
            ", sku='" + getSku() + "'" +
            ", color='" + getColor() + "'" +
            ", talla='" + getTalla() + "'" +
            ", codigoBarras='" + getCodigoBarras() + "'" +
            ", stock=" + getStock() +
            ", stockMinimo=" + getStockMinimo() +
            ", ubicacionBodega='" + getUbicacionBodega() + "'" +
            ", precioAdicional=" + getPrecioAdicional() +
            ", activo='" + getActivo() + "'" +
            ", producto=" + getProducto() +
            "}";
    }
}
