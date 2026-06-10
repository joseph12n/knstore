package com.mycompany.knstore.domain;

import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Carrito.
 */
@Document(collection = "carrito")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Carrito implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @DecimalMin(value = "0")
    @Field("subtotal")
    private BigDecimal subtotal;

    @Field("fecha_actualizacion")
    private Instant fechaActualizacion;

    @DBRef
    @Field("cuenta")
    private Cuenta cuenta;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Carrito id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getSubtotal() {
        return this.subtotal;
    }

    public Carrito subtotal(BigDecimal subtotal) {
        this.setSubtotal(subtotal);
        return this;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Instant getFechaActualizacion() {
        return this.fechaActualizacion;
    }

    public Carrito fechaActualizacion(Instant fechaActualizacion) {
        this.setFechaActualizacion(fechaActualizacion);
        return this;
    }

    public void setFechaActualizacion(Instant fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Cuenta getCuenta() {
        return this.cuenta;
    }

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }

    public Carrito cuenta(Cuenta cuenta) {
        this.setCuenta(cuenta);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Carrito)) {
            return false;
        }
        return getId() != null && getId().equals(((Carrito) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Carrito{" +
            "id=" + getId() +
            ", subtotal=" + getSubtotal() +
            ", fechaActualizacion='" + getFechaActualizacion() + "'" +
            "}";
    }
}
