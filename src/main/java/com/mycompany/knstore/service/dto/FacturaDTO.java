package com.mycompany.knstore.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.knstore.domain.Factura} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FacturaDTO implements Serializable {

    private String id;

    @Size(max = 10)
    private String prefijo;

    @Size(max = 96)
    private String cufe;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal subtotal;

    @DecimalMin(value = "0")
    private BigDecimal descuentos;

    @DecimalMin(value = "0")
    private BigDecimal baseGravableIva;

    @DecimalMin(value = "0")
    private BigDecimal valorIva;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal total;

    @Size(max = 500)
    private String notasAdicionales;

    private String codigoQr;

    @NotNull
    private Boolean enviada;

    private Instant fechaEmision;

    private LocalDate fechaVencimiento;

    private Instant fechaEnvioEmail;

    @NotNull
    private PagoDTO pago;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public String getCufe() {
        return cufe;
    }

    public void setCufe(String cufe) {
        this.cufe = cufe;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDescuentos() {
        return descuentos;
    }

    public void setDescuentos(BigDecimal descuentos) {
        this.descuentos = descuentos;
    }

    public BigDecimal getBaseGravableIva() {
        return baseGravableIva;
    }

    public void setBaseGravableIva(BigDecimal baseGravableIva) {
        this.baseGravableIva = baseGravableIva;
    }

    public BigDecimal getValorIva() {
        return valorIva;
    }

    public void setValorIva(BigDecimal valorIva) {
        this.valorIva = valorIva;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getNotasAdicionales() {
        return notasAdicionales;
    }

    public void setNotasAdicionales(String notasAdicionales) {
        this.notasAdicionales = notasAdicionales;
    }

    public String getCodigoQr() {
        return codigoQr;
    }

    public void setCodigoQr(String codigoQr) {
        this.codigoQr = codigoQr;
    }

    public Boolean getEnviada() {
        return enviada;
    }

    public void setEnviada(Boolean enviada) {
        this.enviada = enviada;
    }

    public Instant getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Instant fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Instant getFechaEnvioEmail() {
        return fechaEnvioEmail;
    }

    public void setFechaEnvioEmail(Instant fechaEnvioEmail) {
        this.fechaEnvioEmail = fechaEnvioEmail;
    }

    public PagoDTO getPago() {
        return pago;
    }

    public void setPago(PagoDTO pago) {
        this.pago = pago;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FacturaDTO)) {
            return false;
        }

        FacturaDTO facturaDTO = (FacturaDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, facturaDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FacturaDTO{" +
            "id='" + getId() + "'" +
            ", prefijo='" + getPrefijo() + "'" +
            ", cufe='" + getCufe() + "'" +
            ", subtotal=" + getSubtotal() +
            ", descuentos=" + getDescuentos() +
            ", baseGravableIva=" + getBaseGravableIva() +
            ", valorIva=" + getValorIva() +
            ", total=" + getTotal() +
            ", notasAdicionales='" + getNotasAdicionales() + "'" +
            ", codigoQr='" + getCodigoQr() + "'" +
            ", enviada='" + getEnviada() + "'" +
            ", fechaEmision='" + getFechaEmision() + "'" +
            ", fechaVencimiento='" + getFechaVencimiento() + "'" +
            ", fechaEnvioEmail='" + getFechaEnvioEmail() + "'" +
            ", pago=" + getPago() +
            "}";
    }
}
