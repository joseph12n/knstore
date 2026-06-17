package com.mycompany.knstore.domain;

import com.mycompany.knstore.domain.enumeration.EstadoIVA;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A CategoriaIVA.
 */
@Document(collection = "categoriaiva")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CategoriaIVA implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Size(max = 60)
    @Field("nombre")
    private String nombre;

    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    @Field("porcentaje")
    private BigDecimal porcentaje;

    @NotNull
    @Field("estado")
    private EstadoIVA estado;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public CategoriaIVA id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public CategoriaIVA nombre(String nombre) {
        this.setNombre(nombre);
        return this;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPorcentaje() {
        return this.porcentaje;
    }

    public CategoriaIVA porcentaje(BigDecimal porcentaje) {
        this.setPorcentaje(porcentaje);
        return this;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public EstadoIVA getEstado() {
        return this.estado;
    }

    public CategoriaIVA estado(EstadoIVA estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(EstadoIVA estado) {
        this.estado = estado;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CategoriaIVA)) {
            return false;
        }
        return getId() != null && getId().equals(((CategoriaIVA) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CategoriaIVA{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", porcentaje=" + getPorcentaje() +
            ", estado='" + getEstado() + "'" +
            "}";
    }
}
