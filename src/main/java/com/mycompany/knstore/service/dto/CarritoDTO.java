package com.mycompany.knstore.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.knstore.domain.Carrito} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CarritoDTO implements Serializable {

    private String id;

    @DecimalMin(value = "0")
    private BigDecimal subtotal;

    private Instant fechaActualizacion;

    @NotNull
    private CuentaDTO cuenta;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Instant getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Instant fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public CuentaDTO getCuenta() {
        return cuenta;
    }

    public void setCuenta(CuentaDTO cuenta) {
        this.cuenta = cuenta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CarritoDTO)) {
            return false;
        }

        CarritoDTO carritoDTO = (CarritoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, carritoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CarritoDTO{" +
            "id='" + getId() + "'" +
            ", subtotal=" + getSubtotal() +
            ", fechaActualizacion='" + getFechaActualizacion() + "'" +
            ", cuenta=" + getCuenta() +
            "}";
    }
}
