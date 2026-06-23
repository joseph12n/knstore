package com.mycompany.knstore.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A ItemPedido.
 */
@Document(collection = "item_pedido")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ItemPedido implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Size(max = 200)
    @Field("nombre_producto")
    private String nombreProducto;

    @Size(max = 220)
    @Field("slug_producto")
    private String slugProducto;

    @Size(max = 100)
    @Field("marca_producto")
    private String marcaProducto;

    @Size(max = 100)
    @Field("sku_producto")
    private String skuProducto;

    @Size(max = 50)
    @Field("color_producto")
    private String colorProducto;

    @Size(max = 30)
    @Field("talla_producto")
    private String tallaProducto;

    @NotNull
    @Min(value = 1)
    @Field("cantidad")
    private Integer cantidad;

    @NotNull
    @DecimalMin(value = "0")
    @Field("precio_unitario")
    private BigDecimal precioUnitario;

    @DecimalMin(value = "0")
    @Field("porcentaje_iva")
    private BigDecimal porcentajeIva;

    @DecimalMin(value = "0")
    @Field("valor_iva")
    private BigDecimal valorIva;

    @DecimalMin(value = "0")
    @Field("descuento")
    private BigDecimal descuento;

    @Field("subtotal")
    private BigDecimal subtotal;

    @DBRef
    @Field("pedido")
    @JsonIgnoreProperties(value = { "direccion", "cuenta", "envio" }, allowSetters = true)
    private Pedido pedido;

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

    public ItemPedido id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombreProducto() {
        return this.nombreProducto;
    }

    public ItemPedido nombreProducto(String nombreProducto) {
        this.setNombreProducto(nombreProducto);
        return this;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getSlugProducto() {
        return this.slugProducto;
    }

    public ItemPedido slugProducto(String slugProducto) {
        this.setSlugProducto(slugProducto);
        return this;
    }

    public void setSlugProducto(String slugProducto) {
        this.slugProducto = slugProducto;
    }

    public String getMarcaProducto() {
        return this.marcaProducto;
    }

    public ItemPedido marcaProducto(String marcaProducto) {
        this.setMarcaProducto(marcaProducto);
        return this;
    }

    public void setMarcaProducto(String marcaProducto) {
        this.marcaProducto = marcaProducto;
    }

    public String getSkuProducto() {
        return this.skuProducto;
    }

    public ItemPedido skuProducto(String skuProducto) {
        this.setSkuProducto(skuProducto);
        return this;
    }

    public void setSkuProducto(String skuProducto) {
        this.skuProducto = skuProducto;
    }

    public String getColorProducto() {
        return this.colorProducto;
    }

    public ItemPedido colorProducto(String colorProducto) {
        this.setColorProducto(colorProducto);
        return this;
    }

    public void setColorProducto(String colorProducto) {
        this.colorProducto = colorProducto;
    }

    public String getTallaProducto() {
        return this.tallaProducto;
    }

    public ItemPedido tallaProducto(String tallaProducto) {
        this.setTallaProducto(tallaProducto);
        return this;
    }

    public void setTallaProducto(String tallaProducto) {
        this.tallaProducto = tallaProducto;
    }

    public Integer getCantidad() {
        return this.cantidad;
    }

    public ItemPedido cantidad(Integer cantidad) {
        this.setCantidad(cantidad);
        return this;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return this.precioUnitario;
    }

    public ItemPedido precioUnitario(BigDecimal precioUnitario) {
        this.setPrecioUnitario(precioUnitario);
        return this;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getPorcentajeIva() {
        return this.porcentajeIva;
    }

    public ItemPedido porcentajeIva(BigDecimal porcentajeIva) {
        this.setPorcentajeIva(porcentajeIva);
        return this;
    }

    public void setPorcentajeIva(BigDecimal porcentajeIva) {
        this.porcentajeIva = porcentajeIva;
    }

    public BigDecimal getValorIva() {
        return this.valorIva;
    }

    public ItemPedido valorIva(BigDecimal valorIva) {
        this.setValorIva(valorIva);
        return this;
    }

    public void setValorIva(BigDecimal valorIva) {
        this.valorIva = valorIva;
    }

    public BigDecimal getDescuento() {
        return this.descuento;
    }

    public ItemPedido descuento(BigDecimal descuento) {
        this.setDescuento(descuento);
        return this;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getSubtotal() {
        return this.subtotal;
    }

    public ItemPedido subtotal(BigDecimal subtotal) {
        this.setSubtotal(subtotal);
        return this;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Pedido getPedido() {
        return this.pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public ItemPedido pedido(Pedido pedido) {
        this.setPedido(pedido);
        return this;
    }

    public Producto getProducto() {
        return this.producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public ItemPedido producto(Producto producto) {
        this.setProducto(producto);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemPedido)) {
            return false;
        }
        return getId() != null && getId().equals(((ItemPedido) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ItemPedido{" +
            "id=" + getId() +
            ", nombreProducto='" + getNombreProducto() + "'" +
            ", slugProducto='" + getSlugProducto() + "'" +
            ", marcaProducto='" + getMarcaProducto() + "'" +
            ", skuProducto='" + getSkuProducto() + "'" +
            ", colorProducto='" + getColorProducto() + "'" +
            ", tallaProducto='" + getTallaProducto() + "'" +
            ", cantidad=" + getCantidad() +
            ", precioUnitario=" + getPrecioUnitario() +
            ", porcentajeIva=" + getPorcentajeIva() +
            ", valorIva=" + getValorIva() +
            ", descuento=" + getDescuento() +
            ", subtotal=" + getSubtotal() +
            "}";
    }
}
