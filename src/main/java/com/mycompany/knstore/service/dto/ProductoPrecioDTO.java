package com.mycompany.knstore.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.knstore.domain.ProductoPrecio} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductoPrecioDTO implements Serializable {

    private String id;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal precioCompra;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal precioVenta;

    @DecimalMin(value = "0")
    private BigDecimal precioAdicional;

    private BigDecimal ganancia;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public BigDecimal getPrecioAdicional() {
        return precioAdicional;
    }

    public void setPrecioAdicional(BigDecimal precioAdicional) {
        this.precioAdicional = precioAdicional;
    }

    public BigDecimal getGanancia() {
        return ganancia;
    }

    public void setGanancia(BigDecimal ganancia) {
        this.ganancia = ganancia;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductoPrecioDTO)) {
            return false;
        }

        ProductoPrecioDTO productoPrecioDTO = (ProductoPrecioDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productoPrecioDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductoPrecioDTO{" +
            "id='" + getId() + "'" +
            ", precioCompra=" + getPrecioCompra() +
            ", precioVenta=" + getPrecioVenta() +
            ", precioAdicional=" + getPrecioAdicional() +
            ", ganancia=" + getGanancia() +
            "}";
    }
}
