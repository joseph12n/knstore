package com.mycompany.knstore.service.dto;

import com.mycompany.knstore.domain.enumeration.EstadoIVA;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.knstore.domain.CategoriaIVA} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CategoriaIVADTO implements Serializable {

    private String id;

    @NotNull
    @Size(max = 60)
    private String nombre;

    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    private BigDecimal porcentaje;

    @NotNull
    private EstadoIVA estado;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public EstadoIVA getEstado() {
        return estado;
    }

    public void setEstado(EstadoIVA estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CategoriaIVADTO)) {
            return false;
        }

        CategoriaIVADTO categoriaIVADTO = (CategoriaIVADTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, categoriaIVADTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CategoriaIVADTO{" +
            "id='" + getId() + "'" +
            ", nombre='" + getNombre() + "'" +
            ", porcentaje=" + getPorcentaje() +
            ", estado='" + getEstado() + "'" +
            "}";
    }
}
