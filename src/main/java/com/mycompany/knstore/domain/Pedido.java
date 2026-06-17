package com.mycompany.knstore.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.knstore.domain.enumeration.EstadoPedido;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Pedido.
 */
@Document(collection = "pedido")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Pedido implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Size(max = 30)
    @Field("numero_pedido")
    private String numeroPedido;

    @NotNull
    @Field("estado")
    private EstadoPedido estado;

    @NotNull
    @DecimalMin(value = "0")
    @Field("subtotal")
    private BigDecimal subtotal;

    @DecimalMin(value = "0")
    @Field("descuento")
    private BigDecimal descuento;

    @DecimalMin(value = "0")
    @Field("iva_total")
    private BigDecimal ivaTotal;

    @DecimalMin(value = "0")
    @Field("costo_envio")
    private BigDecimal costoEnvio;

    @NotNull
    @DecimalMin(value = "0")
    @Field("total")
    private BigDecimal total;

    @Size(max = 500)
    @Field("notas_cliente")
    private String notasCliente;

    @Size(max = 500)
    @Field("notas_internas")
    private String notasInternas;

    @Size(max = 45)
    @Field("ip_origen")
    private String ipOrigen;

    @Size(max = 300)
    @Field("user_agent")
    private String userAgent;

    @DBRef
    @Field("direccion")
    private Direccion direccion;

    @DBRef
    @Field("cuenta")
    @JsonIgnoreProperties(value = { "user", "tipoDocumento" }, allowSetters = true)
    private Cuenta cuenta;

    @DBRef
    private Envio envio;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Pedido id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumeroPedido() {
        return this.numeroPedido;
    }

    public Pedido numeroPedido(String numeroPedido) {
        this.setNumeroPedido(numeroPedido);
        return this;
    }

    public void setNumeroPedido(String numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public EstadoPedido getEstado() {
        return this.estado;
    }

    public Pedido estado(EstadoPedido estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public BigDecimal getSubtotal() {
        return this.subtotal;
    }

    public Pedido subtotal(BigDecimal subtotal) {
        this.setSubtotal(subtotal);
        return this;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDescuento() {
        return this.descuento;
    }

    public Pedido descuento(BigDecimal descuento) {
        this.setDescuento(descuento);
        return this;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getIvaTotal() {
        return this.ivaTotal;
    }

    public Pedido ivaTotal(BigDecimal ivaTotal) {
        this.setIvaTotal(ivaTotal);
        return this;
    }

    public void setIvaTotal(BigDecimal ivaTotal) {
        this.ivaTotal = ivaTotal;
    }

    public BigDecimal getCostoEnvio() {
        return this.costoEnvio;
    }

    public Pedido costoEnvio(BigDecimal costoEnvio) {
        this.setCostoEnvio(costoEnvio);
        return this;
    }

    public void setCostoEnvio(BigDecimal costoEnvio) {
        this.costoEnvio = costoEnvio;
    }

    public BigDecimal getTotal() {
        return this.total;
    }

    public Pedido total(BigDecimal total) {
        this.setTotal(total);
        return this;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getNotasCliente() {
        return this.notasCliente;
    }

    public Pedido notasCliente(String notasCliente) {
        this.setNotasCliente(notasCliente);
        return this;
    }

    public void setNotasCliente(String notasCliente) {
        this.notasCliente = notasCliente;
    }

    public String getNotasInternas() {
        return this.notasInternas;
    }

    public Pedido notasInternas(String notasInternas) {
        this.setNotasInternas(notasInternas);
        return this;
    }

    public void setNotasInternas(String notasInternas) {
        this.notasInternas = notasInternas;
    }

    public String getIpOrigen() {
        return this.ipOrigen;
    }

    public Pedido ipOrigen(String ipOrigen) {
        this.setIpOrigen(ipOrigen);
        return this;
    }

    public void setIpOrigen(String ipOrigen) {
        this.ipOrigen = ipOrigen;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public Pedido userAgent(String userAgent) {
        this.setUserAgent(userAgent);
        return this;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Direccion getDireccion() {
        return this.direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public Pedido direccion(Direccion direccion) {
        this.setDireccion(direccion);
        return this;
    }

    public Cuenta getCuenta() {
        return this.cuenta;
    }

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }

    public Pedido cuenta(Cuenta cuenta) {
        this.setCuenta(cuenta);
        return this;
    }

    public Envio getEnvio() {
        return this.envio;
    }

    public void setEnvio(Envio envio) {
        if (this.envio != null) {
            this.envio.setPedido(null);
        }
        if (envio != null) {
            envio.setPedido(this);
        }
        this.envio = envio;
    }

    public Pedido envio(Envio envio) {
        this.setEnvio(envio);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pedido)) {
            return false;
        }
        return getId() != null && getId().equals(((Pedido) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Pedido{" +
            "id=" + getId() +
            ", numeroPedido='" + getNumeroPedido() + "'" +
            ", estado='" + getEstado() + "'" +
            ", subtotal=" + getSubtotal() +
            ", descuento=" + getDescuento() +
            ", ivaTotal=" + getIvaTotal() +
            ", costoEnvio=" + getCostoEnvio() +
            ", total=" + getTotal() +
            ", notasCliente='" + getNotasCliente() + "'" +
            ", notasInternas='" + getNotasInternas() + "'" +
            ", ipOrigen='" + getIpOrigen() + "'" +
            ", userAgent='" + getUserAgent() + "'" +
            "}";
    }
}
