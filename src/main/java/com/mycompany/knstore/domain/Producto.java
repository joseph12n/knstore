package com.mycompany.knstore.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.knstore.domain.enumeration.CategoriaIVA;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Producto.
 */
@Document(collection = "producto")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Producto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Size(max = 200)
    @Field("nombre")
    private String nombre;

    @NotNull
    @Size(max = 220)
    @Field("slug")
    private String slug;

    @Field("descripcion")
    private String descripcion;

    @Field("imagen")
    private byte[] imagen;

    @Field("imagen_content_type")
    private String imagenContentType;

    @Size(max = 200)
    @Field("imagen_alt")
    private String imagenAlt;

    @Size(max = 100)
    @Field("marca")
    private String marca;

    @Size(max = 60)
    @Field("referencia")
    private String referencia;

    @Size(max = 50)
    @Field("codigo_barras")
    private String codigoBarras;

    @Size(max = 20)
    @Field("unidad_medida")
    private String unidadMedida;

    @DecimalMin(value = "0")
    @Field("peso_kg")
    private BigDecimal pesoKg;

    @DecimalMin(value = "0")
    @Field("largo_cm")
    private BigDecimal largoCm;

    @DecimalMin(value = "0")
    @Field("ancho_cm")
    private BigDecimal anchoCm;

    @DecimalMin(value = "0")
    @Field("alto_cm")
    private BigDecimal altoCm;

    @NotNull
    @Field("categoria_iva")
    private CategoriaIVA categoriaIva;

    @NotNull
    @DecimalMin(value = "0")
    @Field("precio_compra")
    private BigDecimal precioCompra;

    @NotNull
    @DecimalMin(value = "0")
    @Field("precio_venta")
    private BigDecimal precioVenta;

    @Field("ganancia")
    private BigDecimal ganancia;

    @Field("margen")
    private BigDecimal margen;

    @Min(value = 0)
    @Field("garantia_meses")
    private Integer garantiaMeses;

    @NotNull
    @Field("destacado")
    private Boolean destacado;

    @NotNull
    @Field("activo")
    private Boolean activo;

    @DBRef
    @Field("subcategoria")
    @JsonIgnoreProperties(value = { "categoria" }, allowSetters = true)
    private Subcategoria subcategoria;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Producto id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public Producto nombre(String nombre) {
        this.setNombre(nombre);
        return this;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSlug() {
        return this.slug;
    }

    public Producto slug(String slug) {
        this.setSlug(slug);
        return this;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public Producto descripcion(String descripcion) {
        this.setDescripcion(descripcion);
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public byte[] getImagen() {
        return this.imagen;
    }

    public Producto imagen(byte[] imagen) {
        this.setImagen(imagen);
        return this;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public String getImagenContentType() {
        return this.imagenContentType;
    }

    public Producto imagenContentType(String imagenContentType) {
        this.imagenContentType = imagenContentType;
        return this;
    }

    public void setImagenContentType(String imagenContentType) {
        this.imagenContentType = imagenContentType;
    }

    public String getImagenAlt() {
        return this.imagenAlt;
    }

    public Producto imagenAlt(String imagenAlt) {
        this.setImagenAlt(imagenAlt);
        return this;
    }

    public void setImagenAlt(String imagenAlt) {
        this.imagenAlt = imagenAlt;
    }

    public String getMarca() {
        return this.marca;
    }

    public Producto marca(String marca) {
        this.setMarca(marca);
        return this;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getReferencia() {
        return this.referencia;
    }

    public Producto referencia(String referencia) {
        this.setReferencia(referencia);
        return this;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getCodigoBarras() {
        return this.codigoBarras;
    }

    public Producto codigoBarras(String codigoBarras) {
        this.setCodigoBarras(codigoBarras);
        return this;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getUnidadMedida() {
        return this.unidadMedida;
    }

    public Producto unidadMedida(String unidadMedida) {
        this.setUnidadMedida(unidadMedida);
        return this;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public BigDecimal getPesoKg() {
        return this.pesoKg;
    }

    public Producto pesoKg(BigDecimal pesoKg) {
        this.setPesoKg(pesoKg);
        return this;
    }

    public void setPesoKg(BigDecimal pesoKg) {
        this.pesoKg = pesoKg;
    }

    public BigDecimal getLargoCm() {
        return this.largoCm;
    }

    public Producto largoCm(BigDecimal largoCm) {
        this.setLargoCm(largoCm);
        return this;
    }

    public void setLargoCm(BigDecimal largoCm) {
        this.largoCm = largoCm;
    }

    public BigDecimal getAnchoCm() {
        return this.anchoCm;
    }

    public Producto anchoCm(BigDecimal anchoCm) {
        this.setAnchoCm(anchoCm);
        return this;
    }

    public void setAnchoCm(BigDecimal anchoCm) {
        this.anchoCm = anchoCm;
    }

    public BigDecimal getAltoCm() {
        return this.altoCm;
    }

    public Producto altoCm(BigDecimal altoCm) {
        this.setAltoCm(altoCm);
        return this;
    }

    public void setAltoCm(BigDecimal altoCm) {
        this.altoCm = altoCm;
    }

    public CategoriaIVA getCategoriaIva() {
        return this.categoriaIva;
    }

    public Producto categoriaIva(CategoriaIVA categoriaIva) {
        this.setCategoriaIva(categoriaIva);
        return this;
    }

    public void setCategoriaIva(CategoriaIVA categoriaIva) {
        this.categoriaIva = categoriaIva;
    }

    public BigDecimal getPrecioCompra() {
        return this.precioCompra;
    }

    public Producto precioCompra(BigDecimal precioCompra) {
        this.setPrecioCompra(precioCompra);
        return this;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public BigDecimal getPrecioVenta() {
        return this.precioVenta;
    }

    public Producto precioVenta(BigDecimal precioVenta) {
        this.setPrecioVenta(precioVenta);
        return this;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public BigDecimal getGanancia() {
        return this.ganancia;
    }

    public Producto ganancia(BigDecimal ganancia) {
        this.setGanancia(ganancia);
        return this;
    }

    public void setGanancia(BigDecimal ganancia) {
        this.ganancia = ganancia;
    }

    public BigDecimal getMargen() {
        return this.margen;
    }

    public Producto margen(BigDecimal margen) {
        this.setMargen(margen);
        return this;
    }

    public void setMargen(BigDecimal margen) {
        this.margen = margen;
    }

    public Integer getGarantiaMeses() {
        return this.garantiaMeses;
    }

    public Producto garantiaMeses(Integer garantiaMeses) {
        this.setGarantiaMeses(garantiaMeses);
        return this;
    }

    public void setGarantiaMeses(Integer garantiaMeses) {
        this.garantiaMeses = garantiaMeses;
    }

    public Boolean getDestacado() {
        return this.destacado;
    }

    public Producto destacado(Boolean destacado) {
        this.setDestacado(destacado);
        return this;
    }

    public void setDestacado(Boolean destacado) {
        this.destacado = destacado;
    }

    public Boolean getActivo() {
        return this.activo;
    }

    public Producto activo(Boolean activo) {
        this.setActivo(activo);
        return this;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Subcategoria getSubcategoria() {
        return this.subcategoria;
    }

    public void setSubcategoria(Subcategoria subcategoria) {
        this.subcategoria = subcategoria;
    }

    public Producto subcategoria(Subcategoria subcategoria) {
        this.setSubcategoria(subcategoria);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Producto)) {
            return false;
        }
        return getId() != null && getId().equals(((Producto) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Producto{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", slug='" + getSlug() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", imagen='" + getImagen() + "'" +
            ", imagenContentType='" + getImagenContentType() + "'" +
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
            "}";
    }
}
