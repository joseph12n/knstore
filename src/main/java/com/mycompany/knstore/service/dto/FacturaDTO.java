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

    @NotNull
    @Size(max = 50)
    private String referencia;

    @Size(max = 96)
    private String cufe;

    @Size(max = 50)
    private String resolucionDian;

    private LocalDate fechaVigenciaResolucion;

    @Size(max = 10)
    private String prefijo;

    private Long consecutivo;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal subtotal;

    @DecimalMin(value = "0")
    private BigDecimal descuentos;

    @DecimalMin(value = "0")
    private BigDecimal baseGravableIva;

    @DecimalMin(value = "0")
    private BigDecimal valorIva;

    @DecimalMin(value = "0")
    private BigDecimal retefuente;

    @DecimalMin(value = "0")
    private BigDecimal reteIva;

    @DecimalMin(value = "0")
    private BigDecimal reteIca;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal total;

    private Instant fechaEmision;

    private LocalDate fechaVencimiento;

    @Size(max = 500)
    private String notasAdicionales;

    private String codigoQr;

    @NotNull
    private Boolean enviada;

    private Instant fechaEnvioEmail;

    @NotNull
    private PedidoDTO pedido;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getCufe() {
        return cufe;
    }

    public void setCufe(String cufe) {
        this.cufe = cufe;
    }

    public String getResolucionDian() {
        return resolucionDian;
    }

    public void setResolucionDian(String resolucionDian) {
        this.resolucionDian = resolucionDian;
    }

    public LocalDate getFechaVigenciaResolucion() {
        return fechaVigenciaResolucion;
    }

    public void setFechaVigenciaResolucion(LocalDate fechaVigenciaResolucion) {
        this.fechaVigenciaResolucion = fechaVigenciaResolucion;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public Long getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(Long consecutivo) {
        this.consecutivo = consecutivo;
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

    public BigDecimal getRetefuente() {
        return retefuente;
    }

    public void setRetefuente(BigDecimal retefuente) {
        this.retefuente = retefuente;
    }

    public BigDecimal getReteIva() {
        return reteIva;
    }

    public void setReteIva(BigDecimal reteIva) {
        this.reteIva = reteIva;
    }

    public BigDecimal getReteIca() {
        return reteIca;
    }

    public void setReteIca(BigDecimal reteIca) {
        this.reteIca = reteIca;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
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

    public Instant getFechaEnvioEmail() {
        return fechaEnvioEmail;
    }

    public void setFechaEnvioEmail(Instant fechaEnvioEmail) {
        this.fechaEnvioEmail = fechaEnvioEmail;
    }

    public PedidoDTO getPedido() {
        return pedido;
    }

    public void setPedido(PedidoDTO pedido) {
        this.pedido = pedido;
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
            ", referencia='" + getReferencia() + "'" +
            ", cufe='" + getCufe() + "'" +
            ", resolucionDian='" + getResolucionDian() + "'" +
            ", fechaVigenciaResolucion='" + getFechaVigenciaResolucion() + "'" +
            ", prefijo='" + getPrefijo() + "'" +
            ", consecutivo=" + getConsecutivo() +
            ", subtotal=" + getSubtotal() +
            ", descuentos=" + getDescuentos() +
            ", baseGravableIva=" + getBaseGravableIva() +
            ", valorIva=" + getValorIva() +
            ", retefuente=" + getRetefuente() +
            ", reteIva=" + getReteIva() +
            ", reteIca=" + getReteIca() +
            ", total=" + getTotal() +
            ", fechaEmision='" + getFechaEmision() + "'" +
            ", fechaVencimiento='" + getFechaVencimiento() + "'" +
            ", notasAdicionales='" + getNotasAdicionales() + "'" +
            ", codigoQr='" + getCodigoQr() + "'" +
            ", enviada='" + getEnviada() + "'" +
            ", fechaEnvioEmail='" + getFechaEnvioEmail() + "'" +
            ", pedido=" + getPedido() +
            "}";
    }
}
