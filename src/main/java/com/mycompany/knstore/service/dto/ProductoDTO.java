package com.mycompany.knstore.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.knstore.domain.Producto} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductoDTO implements Serializable {

    private String id;

    @NotNull
    @Size(max = 200)
    private String nombre;

    @NotNull
    @Size(max = 220)
    private String slug;

    @Size(max = 60)
    private String referencia;

    @NotNull
    @Size(max = 100)
    private String sku;

    @Size(max = 50)
    private String color;

    @Size(max = 30)
    private String talla;

    @Size(max = 50)
    private String codigoBarras;

    @Size(max = 20)
    private String unidadMedida;

    private String descripcion;

    @NotNull
    private Boolean destacado;

    @NotNull
    private Boolean activo;

    private ProductoPrecioDTO precio;

    private ProductoInventarioDTO inventario;

    @NotNull
    private CategoriaDTO categoria;

    @NotNull
    private SubcategoriaDTO subcategoria;

    private MarcaDTO marca;

    private CategoriaIVADTO categoriaIva;

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

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getDestacado() {
        return destacado;
    }

    public void setDestacado(Boolean destacado) {
        this.destacado = destacado;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public ProductoPrecioDTO getPrecio() {
        return precio;
    }

    public void setPrecio(ProductoPrecioDTO precio) {
        this.precio = precio;
    }

    public ProductoInventarioDTO getInventario() {
        return inventario;
    }

    public void setInventario(ProductoInventarioDTO inventario) {
        this.inventario = inventario;
    }

    public CategoriaDTO getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaDTO categoria) {
        this.categoria = categoria;
    }

    public SubcategoriaDTO getSubcategoria() {
        return subcategoria;
    }

    public void setSubcategoria(SubcategoriaDTO subcategoria) {
        this.subcategoria = subcategoria;
    }

    public MarcaDTO getMarca() {
        return marca;
    }

    public void setMarca(MarcaDTO marca) {
        this.marca = marca;
    }

    public CategoriaIVADTO getCategoriaIva() {
        return categoriaIva;
    }

    public void setCategoriaIva(CategoriaIVADTO categoriaIva) {
        this.categoriaIva = categoriaIva;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductoDTO)) {
            return false;
        }

        ProductoDTO productoDTO = (ProductoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductoDTO{" +
            "id='" + getId() + "'" +
            ", nombre='" + getNombre() + "'" +
            ", slug='" + getSlug() + "'" +
            ", referencia='" + getReferencia() + "'" +
            ", sku='" + getSku() + "'" +
            ", color='" + getColor() + "'" +
            ", talla='" + getTalla() + "'" +
            ", codigoBarras='" + getCodigoBarras() + "'" +
            ", unidadMedida='" + getUnidadMedida() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", destacado='" + getDestacado() + "'" +
            ", activo='" + getActivo() + "'" +
            ", precio=" + getPrecio() +
            ", inventario=" + getInventario() +
            ", categoria=" + getCategoria() +
            ", subcategoria=" + getSubcategoria() +
            ", marca=" + getMarca() +
            ", categoriaIva=" + getCategoriaIva() +
            "}";
    }
}
