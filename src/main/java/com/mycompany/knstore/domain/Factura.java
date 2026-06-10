package com.mycompany.knstore.domain;

import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Factura.
 */
@Document(collection = "factura")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Factura implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Size(max = 50)
    @Field("referencia")
    private String referencia;

    @Size(max = 96)
    @Field("cufe")
    private String cufe;

    @Size(max = 50)
    @Field("resolucion_dian")
    private String resolucionDian;

    @Field("fecha_vigencia_resolucion")
    private LocalDate fechaVigenciaResolucion;

    @Size(max = 10)
    @Field("prefijo")
    private String prefijo;

    @Field("consecutivo")
    private Long consecutivo;

    @NotNull
    @DecimalMin(value = "0")
    @Field("subtotal")
    private BigDecimal subtotal;

    @DecimalMin(value = "0")
    @Field("descuentos")
    private BigDecimal descuentos;

    @DecimalMin(value = "0")
    @Field("base_gravable_iva")
    private BigDecimal baseGravableIva;

    @DecimalMin(value = "0")
    @Field("valor_iva")
    private BigDecimal valorIva;

    @DecimalMin(value = "0")
    @Field("retefuente")
    private BigDecimal retefuente;

    @DecimalMin(value = "0")
    @Field("rete_iva")
    private BigDecimal reteIva;

    @DecimalMin(value = "0")
    @Field("rete_ica")
    private BigDecimal reteIca;

    @NotNull
    @DecimalMin(value = "0")
    @Field("total")
    private BigDecimal total;

    @Field("fecha_emision")
    private Instant fechaEmision;

    @Field("fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Size(max = 500)
    @Field("notas_adicionales")
    private String notasAdicionales;

    @Field("codigo_qr")
    private String codigoQr;

    @NotNull
    @Field("enviada")
    private Boolean enviada;

    @Field("fecha_envio_email")
    private Instant fechaEnvioEmail;

    @DBRef
    @Field("pedido")
    private Pedido pedido;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Factura id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReferencia() {
        return this.referencia;
    }

    public Factura referencia(String referencia) {
        this.setReferencia(referencia);
        return this;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getCufe() {
        return this.cufe;
    }

    public Factura cufe(String cufe) {
        this.setCufe(cufe);
        return this;
    }

    public void setCufe(String cufe) {
        this.cufe = cufe;
    }

    public String getResolucionDian() {
        return this.resolucionDian;
    }

    public Factura resolucionDian(String resolucionDian) {
        this.setResolucionDian(resolucionDian);
        return this;
    }

    public void setResolucionDian(String resolucionDian) {
        this.resolucionDian = resolucionDian;
    }

    public LocalDate getFechaVigenciaResolucion() {
        return this.fechaVigenciaResolucion;
    }

    public Factura fechaVigenciaResolucion(LocalDate fechaVigenciaResolucion) {
        this.setFechaVigenciaResolucion(fechaVigenciaResolucion);
        return this;
    }

    public void setFechaVigenciaResolucion(LocalDate fechaVigenciaResolucion) {
        this.fechaVigenciaResolucion = fechaVigenciaResolucion;
    }

    public String getPrefijo() {
        return this.prefijo;
    }

    public Factura prefijo(String prefijo) {
        this.setPrefijo(prefijo);
        return this;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public Long getConsecutivo() {
        return this.consecutivo;
    }

    public Factura consecutivo(Long consecutivo) {
        this.setConsecutivo(consecutivo);
        return this;
    }

    public void setConsecutivo(Long consecutivo) {
        this.consecutivo = consecutivo;
    }

    public BigDecimal getSubtotal() {
        return this.subtotal;
    }

    public Factura subtotal(BigDecimal subtotal) {
        this.setSubtotal(subtotal);
        return this;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDescuentos() {
        return this.descuentos;
    }

    public Factura descuentos(BigDecimal descuentos) {
        this.setDescuentos(descuentos);
        return this;
    }

    public void setDescuentos(BigDecimal descuentos) {
        this.descuentos = descuentos;
    }

    public BigDecimal getBaseGravableIva() {
        return this.baseGravableIva;
    }

    public Factura baseGravableIva(BigDecimal baseGravableIva) {
        this.setBaseGravableIva(baseGravableIva);
        return this;
    }

    public void setBaseGravableIva(BigDecimal baseGravableIva) {
        this.baseGravableIva = baseGravableIva;
    }

    public BigDecimal getValorIva() {
        return this.valorIva;
    }

    public Factura valorIva(BigDecimal valorIva) {
        this.setValorIva(valorIva);
        return this;
    }

    public void setValorIva(BigDecimal valorIva) {
        this.valorIva = valorIva;
    }

    public BigDecimal getRetefuente() {
        return this.retefuente;
    }

    public Factura retefuente(BigDecimal retefuente) {
        this.setRetefuente(retefuente);
        return this;
    }

    public void setRetefuente(BigDecimal retefuente) {
        this.retefuente = retefuente;
    }

    public BigDecimal getReteIva() {
        return this.reteIva;
    }

    public Factura reteIva(BigDecimal reteIva) {
        this.setReteIva(reteIva);
        return this;
    }

    public void setReteIva(BigDecimal reteIva) {
        this.reteIva = reteIva;
    }

    public BigDecimal getReteIca() {
        return this.reteIca;
    }

    public Factura reteIca(BigDecimal reteIca) {
        this.setReteIca(reteIca);
        return this;
    }

    public void setReteIca(BigDecimal reteIca) {
        this.reteIca = reteIca;
    }

    public BigDecimal getTotal() {
        return this.total;
    }

    public Factura total(BigDecimal total) {
        this.setTotal(total);
        return this;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Instant getFechaEmision() {
        return this.fechaEmision;
    }

    public Factura fechaEmision(Instant fechaEmision) {
        this.setFechaEmision(fechaEmision);
        return this;
    }

    public void setFechaEmision(Instant fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public LocalDate getFechaVencimiento() {
        return this.fechaVencimiento;
    }

    public Factura fechaVencimiento(LocalDate fechaVencimiento) {
        this.setFechaVencimiento(fechaVencimiento);
        return this;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getNotasAdicionales() {
        return this.notasAdicionales;
    }

    public Factura notasAdicionales(String notasAdicionales) {
        this.setNotasAdicionales(notasAdicionales);
        return this;
    }

    public void setNotasAdicionales(String notasAdicionales) {
        this.notasAdicionales = notasAdicionales;
    }

    public String getCodigoQr() {
        return this.codigoQr;
    }

    public Factura codigoQr(String codigoQr) {
        this.setCodigoQr(codigoQr);
        return this;
    }

    public void setCodigoQr(String codigoQr) {
        this.codigoQr = codigoQr;
    }

    public Boolean getEnviada() {
        return this.enviada;
    }

    public Factura enviada(Boolean enviada) {
        this.setEnviada(enviada);
        return this;
    }

    public void setEnviada(Boolean enviada) {
        this.enviada = enviada;
    }

    public Instant getFechaEnvioEmail() {
        return this.fechaEnvioEmail;
    }

    public Factura fechaEnvioEmail(Instant fechaEnvioEmail) {
        this.setFechaEnvioEmail(fechaEnvioEmail);
        return this;
    }

    public void setFechaEnvioEmail(Instant fechaEnvioEmail) {
        this.fechaEnvioEmail = fechaEnvioEmail;
    }

    public Pedido getPedido() {
        return this.pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Factura pedido(Pedido pedido) {
        this.setPedido(pedido);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Factura)) {
            return false;
        }
        return getId() != null && getId().equals(((Factura) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Factura{" +
            "id=" + getId() +
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
            "}";
    }
}
