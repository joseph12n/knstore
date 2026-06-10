package com.mycompany.knstore.service.dto;

import com.mycompany.knstore.domain.enumeration.EstadoEnvio;
import com.mycompany.knstore.domain.enumeration.TipoServicioEnvio;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.knstore.domain.Envio} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EnvioDTO implements Serializable {

    private String id;

    @Size(max = 100)
    private String numeroRastreo;

    @Size(max = 100)
    private String transportadora;

    private TipoServicioEnvio tipoServicio;

    @NotNull
    private EstadoEnvio estado;

    @DecimalMin(value = "0")
    private BigDecimal costoEnvio;

    @DecimalMin(value = "0")
    private BigDecimal pesoKg;

    @DecimalMin(value = "0")
    private BigDecimal valorDeclarado;

    @Size(max = 300)
    private String urlRastreo;

    @Size(max = 300)
    private String observaciones;

    private Instant fechaDespacho;

    private Instant fechaEntregaEstimada;

    private Instant fechaEntrega;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumeroRastreo() {
        return numeroRastreo;
    }

    public void setNumeroRastreo(String numeroRastreo) {
        this.numeroRastreo = numeroRastreo;
    }

    public String getTransportadora() {
        return transportadora;
    }

    public void setTransportadora(String transportadora) {
        this.transportadora = transportadora;
    }

    public TipoServicioEnvio getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(TipoServicioEnvio tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public EstadoEnvio getEstado() {
        return estado;
    }

    public void setEstado(EstadoEnvio estado) {
        this.estado = estado;
    }

    public BigDecimal getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(BigDecimal costoEnvio) {
        this.costoEnvio = costoEnvio;
    }

    public BigDecimal getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(BigDecimal pesoKg) {
        this.pesoKg = pesoKg;
    }

    public BigDecimal getValorDeclarado() {
        return valorDeclarado;
    }

    public void setValorDeclarado(BigDecimal valorDeclarado) {
        this.valorDeclarado = valorDeclarado;
    }

    public String getUrlRastreo() {
        return urlRastreo;
    }

    public void setUrlRastreo(String urlRastreo) {
        this.urlRastreo = urlRastreo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Instant getFechaDespacho() {
        return fechaDespacho;
    }

    public void setFechaDespacho(Instant fechaDespacho) {
        this.fechaDespacho = fechaDespacho;
    }

    public Instant getFechaEntregaEstimada() {
        return fechaEntregaEstimada;
    }

    public void setFechaEntregaEstimada(Instant fechaEntregaEstimada) {
        this.fechaEntregaEstimada = fechaEntregaEstimada;
    }

    public Instant getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Instant fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnvioDTO)) {
            return false;
        }

        EnvioDTO envioDTO = (EnvioDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, envioDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EnvioDTO{" +
            "id='" + getId() + "'" +
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
