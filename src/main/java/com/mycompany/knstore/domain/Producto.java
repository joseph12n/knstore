package com.mycompany.knstore.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
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

    @Size(max = 60)
    @Field("referencia")
    private String referencia;

    @NotNull
    @Size(max = 100)
    @Field("sku")
    private String sku;

    @Size(max = 50)
    @Field("color")
    private String color;

    @Size(max = 30)
    @Field("talla")
    private String talla;

    @Size(max = 50)
    @Field("codigo_barras")
    private String codigoBarras;

    @Size(max = 20)
    @Field("unidad_medida")
    private String unidadMedida;

    @Field("descripcion")
    private String descripcion;

    @NotNull
    @Field("destacado")
    private Boolean destacado;

    @NotNull
    @Field("activo")
    private Boolean activo;

    @DBRef
    @Field("precio")
    private ProductoPrecio precio;

    @DBRef
    @Field("inventario")
    private ProductoInventario inventario;

    @DBRef
    @Field("imagenes")
    @JsonIgnoreProperties(value = { "producto" }, allowSetters = true)
    private Set<ProductoImagen> imageneses = new HashSet<>();

    @DBRef
    @Field("categoria")
    private Categoria categoria;

    @DBRef
    @Field("subcategoria")
    @JsonIgnoreProperties(value = { "categoria" }, allowSetters = true)
    private Subcategoria subcategoria;

    @DBRef
    @Field("marca")
    private Marca marca;

    @DBRef
    @Field("categoriaIva")
    private CategoriaIVA categoriaIva;

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

    public String getSku() {
        return this.sku;
    }

    public Producto sku(String sku) {
        this.setSku(sku);
        return this;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getColor() {
        return this.color;
    }

    public Producto color(String color) {
        this.setColor(color);
        return this;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTalla() {
        return this.talla;
    }

    public Producto talla(String talla) {
        this.setTalla(talla);
        return this;
    }

    public void setTalla(String talla) {
        this.talla = talla;
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

    public ProductoPrecio getPrecio() {
        return this.precio;
    }

    public void setPrecio(ProductoPrecio productoPrecio) {
        this.precio = productoPrecio;
    }

    public Producto precio(ProductoPrecio productoPrecio) {
        this.setPrecio(productoPrecio);
        return this;
    }

    public ProductoInventario getInventario() {
        return this.inventario;
    }

    public void setInventario(ProductoInventario productoInventario) {
        this.inventario = productoInventario;
    }

    public Producto inventario(ProductoInventario productoInventario) {
        this.setInventario(productoInventario);
        return this;
    }

    public Set<ProductoImagen> getImageneses() {
        return this.imageneses;
    }

    public void setImageneses(Set<ProductoImagen> productoImagens) {
        if (this.imageneses != null) {
            this.imageneses.forEach(i -> i.setProducto(null));
        }
        if (productoImagens != null) {
            productoImagens.forEach(i -> i.setProducto(this));
        }
        this.imageneses = productoImagens;
    }

    public Producto imageneses(Set<ProductoImagen> productoImagens) {
        this.setImageneses(productoImagens);
        return this;
    }

    public Producto addImagenes(ProductoImagen productoImagen) {
        this.imageneses.add(productoImagen);
        productoImagen.setProducto(this);
        return this;
    }

    public Producto removeImagenes(ProductoImagen productoImagen) {
        this.imageneses.remove(productoImagen);
        productoImagen.setProducto(null);
        return this;
    }

    public Categoria getCategoria() {
        return this.categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Producto categoria(Categoria categoria) {
        this.setCategoria(categoria);
        return this;
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

    public Marca getMarca() {
        return this.marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public Producto marca(Marca marca) {
        this.setMarca(marca);
        return this;
    }

    public CategoriaIVA getCategoriaIva() {
        return this.categoriaIva;
    }

    public void setCategoriaIva(CategoriaIVA categoriaIVA) {
        this.categoriaIva = categoriaIVA;
    }

    public Producto categoriaIva(CategoriaIVA categoriaIVA) {
        this.setCategoriaIva(categoriaIVA);
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
            ", referencia='" + getReferencia() + "'" +
            ", sku='" + getSku() + "'" +
            ", color='" + getColor() + "'" +
            ", talla='" + getTalla() + "'" +
            ", codigoBarras='" + getCodigoBarras() + "'" +
            ", unidadMedida='" + getUnidadMedida() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", destacado='" + getDestacado() + "'" +
            ", activo='" + getActivo() + "'" +
            "}";
    }
}
