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
 * A ProductoImagen.
 */
@Document(collection = "producto_imagen")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductoImagen implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("imagen")
    private byte[] imagen;

    @NotNull
    @Field("imagen_content_type")
    private String imagenContentType;

    @Size(max = 200)
    @Field("imagen_alt")
    private String imagenAlt;

    @NotNull
    @Field("es_principal")
    private Boolean esPrincipal;

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

    public ProductoImagen id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getImagen() {
        return this.imagen;
    }

    public ProductoImagen imagen(byte[] imagen) {
        this.setImagen(imagen);
        return this;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public String getImagenContentType() {
        return this.imagenContentType;
    }

    public ProductoImagen imagenContentType(String imagenContentType) {
        this.imagenContentType = imagenContentType;
        return this;
    }

    public void setImagenContentType(String imagenContentType) {
        this.imagenContentType = imagenContentType;
    }

    public String getImagenAlt() {
        return this.imagenAlt;
    }

    public ProductoImagen imagenAlt(String imagenAlt) {
        this.setImagenAlt(imagenAlt);
        return this;
    }

    public void setImagenAlt(String imagenAlt) {
        this.imagenAlt = imagenAlt;
    }

    public Boolean getEsPrincipal() {
        return this.esPrincipal;
    }

    public ProductoImagen esPrincipal(Boolean esPrincipal) {
        this.setEsPrincipal(esPrincipal);
        return this;
    }

    public void setEsPrincipal(Boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
    }

    public Producto getProducto() {
        return this.producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public ProductoImagen producto(Producto producto) {
        this.setProducto(producto);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductoImagen)) {
            return false;
        }
        return getId() != null && getId().equals(((ProductoImagen) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductoImagen{" +
            "id=" + getId() +
            ", imagen='" + getImagen() + "'" +
            ", imagenContentType='" + getImagenContentType() + "'" +
            ", imagenAlt='" + getImagenAlt() + "'" +
            ", esPrincipal='" + getEsPrincipal() + "'" +
            "}";
    }
}
