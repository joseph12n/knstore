package com.mycompany.knstore.domain;

import com.mycompany.knstore.domain.enumeration.EstadoEnvio;
import com.mycompany.knstore.domain.enumeration.TipoServicioEnvio;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Envio.
 */
@Document(collection = "envio")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Envio implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Size(max = 100)
    @Field("numero_rastreo")
    private String numeroRastreo;

    @Size(max = 100)
    @Field("transportadora")
    private String transportadora;

    @Field("tipo_servicio")
    private TipoServicioEnvio tipoServicio;

    @NotNull
    @Field("estado")
    private EstadoEnvio estado;

    @DecimalMin(value = "0")
    @Field("costo_envio")
    private BigDecimal costoEnvio;

    @DecimalMin(value = "0")
    @Field("peso_kg")
    private BigDecimal pesoKg;

    @DecimalMin(value = "0")
    @Field("valor_declarado")
    private BigDecimal valorDeclarado;

    @Size(max = 300)
    @Field("url_rastreo")
    private String urlRastreo;

    @Size(max = 300)
    @Field("observaciones")
    private String observaciones;

    @Field("fecha_despacho")
    private Instant fechaDespacho;

    @Field("fecha_entrega_estimada")
    private Instant fechaEntregaEstimada;

    @Field("fecha_entrega")
    private Instant fechaEntrega;

    @DBRef
    private Pedido pedido;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Envio id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumeroRastreo() {
        return this.numeroRastreo;
    }

    public Envio numeroRastreo(String numeroRastreo) {
        this.setNumeroRastreo(numeroRastreo);
        return this;
    }

    public void setNumeroRastreo(String numeroRastreo) {
        this.numeroRastreo = numeroRastreo;
    }

    public String getTransportadora() {
        return this.transportadora;
    }

    public Envio transportadora(String transportadora) {
        this.setTransportadora(transportadora);
        return this;
    }

    public void setTransportadora(String transportadora) {
        this.transportadora = transportadora;
    }

    public TipoServicioEnvio getTipoServicio() {
        return this.tipoServicio;
    }

    public Envio tipoServicio(TipoServicioEnvio tipoServicio) {
        this.setTipoServicio(tipoServicio);
        return this;
    }

    public void setTipoServicio(TipoServicioEnvio tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public EstadoEnvio getEstado() {
        return this.estado;
    }

    public Envio estado(EstadoEnvio estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(EstadoEnvio estado) {
        this.estado = estado;
    }

    public BigDecimal getCostoEnvio() {
        return this.costoEnvio;
    }

    public Envio costoEnvio(BigDecimal costoEnvio) {
        this.setCostoEnvio(costoEnvio);
        return this;
    }

    public void setCostoEnvio(BigDecimal costoEnvio) {
        this.costoEnvio = costoEnvio;
    }

    public BigDecimal getPesoKg() {
        return this.pesoKg;
    }

    public Envio pesoKg(BigDecimal pesoKg) {
        this.setPesoKg(pesoKg);
        return this;
    }

    public void setPesoKg(BigDecimal pesoKg) {
        this.pesoKg = pesoKg;
    }

    public BigDecimal getValorDeclarado() {
        return this.valorDeclarado;
    }

    public Envio valorDeclarado(BigDecimal valorDeclarado) {
        this.setValorDeclarado(valorDeclarado);
        return this;
    }

    public void setValorDeclarado(BigDecimal valorDeclarado) {
        this.valorDeclarado = valorDeclarado;
    }

    public String getUrlRastreo() {
        return this.urlRastreo;
    }

    public Envio urlRastreo(String urlRastreo) {
        this.setUrlRastreo(urlRastreo);
        return this;
    }

    public void setUrlRastreo(String urlRastreo) {
        this.urlRastreo = urlRastreo;
    }

    public String getObservaciones() {
        return this.observaciones;
    }

    public Envio observaciones(String observaciones) {
        this.setObservaciones(observaciones);
        return this;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Instant getFechaDespacho() {
        return this.fechaDespacho;
    }

    public Envio fechaDespacho(Instant fechaDespacho) {
        this.setFechaDespacho(fechaDespacho);
        return this;
    }

    public void setFechaDespacho(Instant fechaDespacho) {
        this.fechaDespacho = fechaDespacho;
    }

    public Instant getFechaEntregaEstimada() {
        return this.fechaEntregaEstimada;
    }

    public Envio fechaEntregaEstimada(Instant fechaEntregaEstimada) {
        this.setFechaEntregaEstimada(fechaEntregaEstimada);
        return this;
    }

    public void setFechaEntregaEstimada(Instant fechaEntregaEstimada) {
        this.fechaEntregaEstimada = fechaEntregaEstimada;
    }

    public Instant getFechaEntrega() {
        return this.fechaEntrega;
    }

    public Envio fechaEntrega(Instant fechaEntrega) {
        this.setFechaEntrega(fechaEntrega);
        return this;
    }

    public void setFechaEntrega(Instant fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public Pedido getPedido() {
        return this.pedido;
    }

    public void setPedido(Pedido pedido) {
        if (this.pedido != null) {
            this.pedido.setEnvio(null);
        }
        if (pedido != null) {
            pedido.setEnvio(this);
        }
        this.pedido = pedido;
    }

    public Envio pedido(Pedido pedido) {
        this.setPedido(pedido);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Envio)) {
            return false;
        }
        return getId() != null && getId().equals(((Envio) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Envio{" +
            "id=" + getId() +
            ", numeroRastreo='" + getNumeroRastreo() + "'" +
            ", transportadora='" + getTransportadora() + "'" +
            ", tipoServicio='" + getTipoServicio() + "'" +
            ", estado='" + getEstado() + "'" +
            ", costoEnvio=" + getCostoEnvio() +
            ", pesoKg=" + getPesoKg() +
            ", valorDeclarado=" + getValorDeclarado() +
            ", urlRastreo='" + getUrlRastreo() + "'" +
            ", observaciones='" + getObservaciones() + "'" +
            ", fechaDespacho='" + getFechaDespacho() + "'" +
            ", fechaEntregaEstimada='" + getFechaEntregaEstimada() + "'" +
            ", fechaEntrega='" + getFechaEntrega() + "'" +
            "}";
    }
}
