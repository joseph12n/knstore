package com.mycompany.knstore.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A EtiquetaProducto.
 */
@Document(collection = "etiqueta_producto")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EtiquetaProducto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Size(max = 80)
    @Field("etiqueta")
    private String etiqueta;

    @DBRef
    @Field("producto")
    @JsonIgnoreProperties(
        value = { "precio", "inventario", "imageneses", "categoria", "subcategoria", "marca", "categoriaIva" },
        allowSetters = true
    )
    private Producto producto;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public EtiquetaProducto id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEtiqueta() {
        return this.etiqueta;
    }

    public EtiquetaProducto etiqueta(String etiqueta) {
        this.setEtiqueta(etiqueta);
        return this;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public Producto getProducto() {
        return this.producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public EtiquetaProducto producto(Producto producto) {
        this.setProducto(producto);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EtiquetaProducto)) {
            return false;
        }
        return getId() != null && getId().equals(((EtiquetaProducto) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EtiquetaProducto{" +
            "id=" + getId() +
            ", etiqueta='" + getEtiqueta() + "'" +
            "}";
    }
}
