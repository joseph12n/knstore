package com.mycompany.knstore.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.knstore.domain.enumeration.EstadoPago;
import com.mycompany.knstore.domain.enumeration.MetodoPago;
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
 * A Pago.
 */
@Document(collection = "pago")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Pago implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("metodo_pago")
    private MetodoPago metodoPago;

    @NotNull
    @Field("estado")
    private EstadoPago estado;

    @NotNull
    @DecimalMin(value = "0")
    @Field("monto")
    private BigDecimal monto;

    @Size(max = 200)
    @Field("referencia_pasarela")
    private String referenciaPasarela;

    @Size(max = 100)
    @Field("codigo_autorizacion")
    private String codigoAutorizacion;

    @Size(max = 300)
    @Field("descripcion_respuesta")
    private String descripcionRespuesta;

    @Min(value = 0)
    @Field("intentos")
    private Integer intentos;

    @Field("fecha_pago")
    private Instant fechaPago;

    @DBRef
    @Field("pedido")
    @JsonIgnoreProperties(value = { "direccion", "cuenta", "envio" }, allowSetters = true)
    private Pedido pedido;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Pago id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MetodoPago getMetodoPago() {
        return this.metodoPago;
    }

    public Pago metodoPago(MetodoPago metodoPago) {
        this.setMetodoPago(metodoPago);
        return this;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public EstadoPago getEstado() {
        return this.estado;
    }

    public Pago estado(EstadoPago estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }

    public BigDecimal getMonto() {
        return this.monto;
    }

    public Pago monto(BigDecimal monto) {
        this.setMonto(monto);
        return this;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getReferenciaPasarela() {
        return this.referenciaPasarela;
    }

    public Pago referenciaPasarela(String referenciaPasarela) {
        this.setReferenciaPasarela(referenciaPasarela);
        return this;
    }

    public void setReferenciaPasarela(String referenciaPasarela) {
        this.referenciaPasarela = referenciaPasarela;
    }

    public String getCodigoAutorizacion() {
        return this.codigoAutorizacion;
    }

    public Pago codigoAutorizacion(String codigoAutorizacion) {
        this.setCodigoAutorizacion(codigoAutorizacion);
        return this;
    }

    public void setCodigoAutorizacion(String codigoAutorizacion) {
        this.codigoAutorizacion = codigoAutorizacion;
    }

    public String getDescripcionRespuesta() {
        return this.descripcionRespuesta;
    }

    public Pago descripcionRespuesta(String descripcionRespuesta) {
        this.setDescripcionRespuesta(descripcionRespuesta);
        return this;
    }

    public void setDescripcionRespuesta(String descripcionRespuesta) {
        this.descripcionRespuesta = descripcionRespuesta;
    }

    public Integer getIntentos() {
        return this.intentos;
    }

    public Pago intentos(Integer intentos) {
        this.setIntentos(intentos);
        return this;
    }

    public void setIntentos(Integer intentos) {
        this.intentos = intentos;
    }

    public Instant getFechaPago() {
        return this.fechaPago;
    }

    public Pago fechaPago(Instant fechaPago) {
        this.setFechaPago(fechaPago);
        return this;
    }

    public void setFechaPago(Instant fechaPago) {
        this.fechaPago = fechaPago;
    }

    public Pedido getPedido() {
        return this.pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Pago pedido(Pedido pedido) {
        this.setPedido(pedido);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pago)) {
            return false;
        }
        return getId() != null && getId().equals(((Pago) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Pago{" +
            "id=" + getId() +
            ", metodoPago='" + getMetodoPago() + "'" +
            ", estado='" + getEstado() + "'" +
            ", monto=" + getMonto() +
            ", referenciaPasarela='" + getReferenciaPasarela() + "'" +
            ", codigoAutorizacion='" + getCodigoAutorizacion() + "'" +
            ", descripcionRespuesta='" + getDescripcionRespuesta() + "'" +
            ", intentos=" + getIntentos() +
            ", fechaPago='" + getFechaPago() + "'" +
            "}";
    }
}
