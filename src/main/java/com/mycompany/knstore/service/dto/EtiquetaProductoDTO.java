package com.mycompany.knstore.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.knstore.domain.EtiquetaProducto} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EtiquetaProductoDTO implements Serializable {

    private String id;

    @NotNull
    @Size(max = 80)
    private String etiqueta;

    @NotNull
    private ProductoDTO producto;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
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
        if (!(o instanceof EtiquetaProductoDTO)) {
            return false;
        }

        EtiquetaProductoDTO etiquetaProductoDTO = (EtiquetaProductoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, etiquetaProductoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EtiquetaProductoDTO{" +
            "id='" + getId() + "'" +
            ", etiqueta='" + getEtiqueta() + "'" +
            ", producto=" + getProducto() +
            "}";
    }
}
