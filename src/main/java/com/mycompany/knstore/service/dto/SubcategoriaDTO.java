package com.mycompany.knstore.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.knstore.domain.Subcategoria} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SubcategoriaDTO implements Serializable {

    private String id;

    @NotNull
    @Size(max = 100)
    private String nombre;

    @NotNull
    @Size(max = 120)
    private String slug;

    private String descripcion;

    private byte[] imagen;

    private String imagenContentType;

    @NotNull
    private Boolean activo;

    @NotNull
    private CategoriaDTO categoria;

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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public CategoriaDTO getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaDTO categoria) {
        this.categoria = categoria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubcategoriaDTO)) {
            return false;
        }

        SubcategoriaDTO subcategoriaDTO = (SubcategoriaDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, subcategoriaDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SubcategoriaDTO{" +
            "id='" + getId() + "'" +
            ", nombre='" + getNombre() + "'" +
            ", slug='" + getSlug() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", imagen='" + getImagen() + "'" +
            ", activo='" + getActivo() + "'" +
            ", categoria=" + getCategoria() +
            "}";
    }
}
