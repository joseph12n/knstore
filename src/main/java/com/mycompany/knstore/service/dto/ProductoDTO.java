package com.mycompany.knstore.service.dto;

import com.mycompany.knstore.domain.enumeration.CategoriaIVA;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
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

    private String descripcion;

    private byte[] imagen;

    private String imagenContentType;

    @Size(max = 200)
    private String imagenAlt;

    @Size(max = 100)
    private String marca;

    @Size(max = 60)
    private String referencia;

    @Size(max = 50)
    private String codigoBarras;

    @Size(max = 20)
    private String unidadMedida;

    @DecimalMin(value = "0")
    private BigDecimal pesoKg;

    @DecimalMin(value = "0")
    private BigDecimal largoCm;

    @DecimalMin(value = "0")
    private BigDecimal anchoCm;

    @DecimalMin(value = "0")
    private BigDecimal altoCm;

    @NotNull
    private CategoriaIVA categoriaIva;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal precioCompra;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal precioVenta;

    private BigDecimal ganancia;

    private BigDecimal margen;

    @Min(value = 0)
    private Integer garantiaMeses;

    @NotNull
    private Boolean destacado;

    @NotNull
    private Boolean activo;

    @NotNull
    private SubcategoriaDTO subcategoria;

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

    public String getImagenAlt() {
        return imagenAlt;
    }

    public void setImagenAlt(String imagenAlt) {
        this.imagenAlt = imagenAlt;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
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

    public BigDecimal getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(BigDecimal pesoKg) {
        this.pesoKg = pesoKg;
    }

    public BigDecimal getLargoCm() {
        return largoCm;
    }

    public void setLargoCm(BigDecimal largoCm) {
        this.largoCm = largoCm;
    }

    public BigDecimal getAnchoCm() {
        return anchoCm;
    }

    public void setAnchoCm(BigDecimal anchoCm) {
        this.anchoCm = anchoCm;
    }

    public BigDecimal getAltoCm() {
        return altoCm;
    }

    public void setAltoCm(BigDecimal altoCm) {
        this.altoCm = altoCm;
    }

    public CategoriaIVA getCategoriaIva() {
        return categoriaIva;
    }

    public void setCategoriaIva(CategoriaIVA categoriaIva) {
        this.categoriaIva = categoriaIva;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public BigDecimal getGanancia() {
        return ganancia;
    }

    public void setGanancia(BigDecimal ganancia) {
        this.ganancia = ganancia;
    }

    public BigDecimal getMargen() {
        return margen;
    }

    public void setMargen(BigDecimal margen) {
        this.margen = margen;
    }

    public Integer getGarantiaMeses() {
        return garantiaMeses;
    }

    public void setGarantiaMeses(Integer garantiaMeses) {
        this.garantiaMeses = garantiaMeses;
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

    public SubcategoriaDTO getSubcategoria() {
        return subcategoria;
    }

    public void setSubcategoria(SubcategoriaDTO subcategoria) {
        this.subcategoria = subcategoria;
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
            ", descripcion='" + getDescripcion() + "'" +
            ", imagen='" + getImagen() + "'" +
            ", imagenAlt='" + getImagenAlt() + "'" +
            ", marca='" + getMarca() + "'" +
            ", referencia='" + getReferencia() + "'" +
            ", codigoBarras='" + getCodigoBarras() + "'" +
            ", unidadMedida='" + getUnidadMedida() + "'" +
            ", pesoKg=" + getPesoKg() +
            ", largoCm=" + getLargoCm() +
            ", anchoCm=" + getAnchoCm() +
            ", altoCm=" + getAltoCm() +
            ", categoriaIva='" + getCategoriaIva() + "'" +
            ", precioCompra=" + getPrecioCompra() +
            ", precioVenta=" + getPrecioVenta() +
            ", ganancia=" + getGanancia() +
            ", margen=" + getMargen() +
            ", garantiaMeses=" + getGarantiaMeses() +
            ", destacado='" + getDestacado() + "'" +
            ", activo='" + getActivo() + "'" +
            ", subcategoria=" + getSubcategoria() +
            "}";
    }
}
