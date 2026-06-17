package com.mycompany.knstore.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @Size(max = 10)
    @Field("prefijo")
    private String prefijo;

    @Size(max = 96)
    @Field("cufe")
    private String cufe;

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

    @NotNull
    @DecimalMin(value = "0")
    @Field("total")
    private BigDecimal total;

    @Size(max = 500)
    @Field("notas_adicionales")
    private String notasAdicionales;

    @Field("codigo_qr")
    private String codigoQr;

    @NotNull
    @Field("enviada")
    private Boolean enviada;

    @Field("fecha_emision")
    private Instant fechaEmision;

    @Field("fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Field("fecha_envio_email")
    private Instant fechaEnvioEmail;

    @DBRef
    @Field("pago")
    @JsonIgnoreProperties(value = { "pedido" }, allowSetters = true)
    private Pago pago;

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

    public Pago getPago() {
        return this.pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    public Factura pago(Pago pago) {
        this.setPago(pago);
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
            "}";
    }
}
