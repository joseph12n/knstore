package com.mycompany.knstore.service.dto;

import com.mycompany.knstore.domain.enumeration.UbicacionBodega;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.knstore.domain.ProductoInventario} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductoInventarioDTO implements Serializable {

    private String id;

    @NotNull
    @Min(value = 0)
    private Integer stock;

    @Min(value = 0)
    private Integer stockMinimo;

    private UbicacionBodega ubicacionBodega;

    @Min(value = 0)
    private Integer garantiaMeses;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public UbicacionBodega getUbicacionBodega() {
        return ubicacionBodega;
    }

    public void setUbicacionBodega(UbicacionBodega ubicacionBodega) {
        this.ubicacionBodega = ubicacionBodega;
    }

    public Integer getGarantiaMeses() {
        return garantiaMeses;
    }

    public void setGarantiaMeses(Integer garantiaMeses) {
        this.garantiaMeses = garantiaMeses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductoInventarioDTO)) {
            return false;
        }

        ProductoInventarioDTO productoInventarioDTO = (ProductoInventarioDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productoInventarioDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductoInventarioDTO{" +
            "id='" + getId() + "'" +
            ", stock=" + getStock() +
            ", stockMinimo=" + getStockMinimo() +
            ", ubicacionBodega='" + getUbicacionBodega() + "'" +
            ", garantiaMeses=" + getGarantiaMeses() +
            "}";
    }
}
