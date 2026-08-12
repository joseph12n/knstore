package com.mycompany.knstore.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.knstore.domain.ProductoImagen} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductoImagenDTO implements Serializable {

    private String id;

    private byte[] imagen;

    private String imagenContentType;

    @Size(max = 200)
    private String imagenAlt;

    @NotNull
    private Boolean esPrincipal;

    @Size(max = 1000)
    private String imagenUrl;

    private ProductoDTO producto;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public String getImagenContentType() {
        return imagenContentType;
    }

    public void setImagenContentType(String imagenContentType) {
        this.imagenContentType = imagenContentType;
    }

    public String getImagenAlt() {
        return imagenAlt;
    }

    public void setImagenAlt(String imagenAlt) {
        this.imagenAlt = imagenAlt;
    }

    public Boolean getEsPrincipal() {
        return esPrincipal;
    }

    public void setEsPrincipal(Boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
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
        if (!(o instanceof ProductoImagenDTO)) {
            return false;
        }

        ProductoImagenDTO productoImagenDTO = (ProductoImagenDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productoImagenDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductoImagenDTO{" +
            "id='" + getId() + "'" +
            ", imagen='" + getImagen() + "'" +
            ", imagenAlt='" + getImagenAlt() + "'" +
            ", esPrincipal='" + getEsPrincipal() + "'" +
            ", imagenUrl='" + getImagenUrl() + "'" +
            ", producto=" + getProducto() +
            "}";
    }
}
