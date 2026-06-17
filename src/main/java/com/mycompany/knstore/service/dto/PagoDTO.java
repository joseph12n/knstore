package com.mycompany.knstore.service.dto;

import com.mycompany.knstore.domain.enumeration.EstadoPago;
import com.mycompany.knstore.domain.enumeration.MetodoPago;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.knstore.domain.Pago} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PagoDTO implements Serializable {

    private String id;

    @NotNull
    private MetodoPago metodoPago;

    @NotNull
    private EstadoPago estado;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal monto;

    @Size(max = 200)
    private String referenciaPasarela;

    @Size(max = 100)
    private String codigoAutorizacion;

    @Size(max = 300)
    private String descripcionRespuesta;

    @Min(value = 0)
    private Integer intentos;

    private Instant fechaPago;

    @NotNull
    private PedidoDTO pedido;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getReferenciaPasarela() {
        return referenciaPasarela;
    }

    public void setReferenciaPasarela(String referenciaPasarela) {
        this.referenciaPasarela = referenciaPasarela;
    }

    public String getCodigoAutorizacion() {
        return codigoAutorizacion;
    }

    public void setCodigoAutorizacion(String codigoAutorizacion) {
        this.codigoAutorizacion = codigoAutorizacion;
    }

    public String getDescripcionRespuesta() {
        return descripcionRespuesta;
    }

    public void setDescripcionRespuesta(String descripcionRespuesta) {
        this.descripcionRespuesta = descripcionRespuesta;
    }

    public Integer getIntentos() {
        return intentos;
    }

    public void setIntentos(Integer intentos) {
        this.intentos = intentos;
    }

    public Instant getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Instant fechaPago) {
        this.fechaPago = fechaPago;
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
        if (!(o instanceof PagoDTO)) {
            return false;
        }

        PagoDTO pagoDTO = (PagoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, pagoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PagoDTO{" +
            "id='" + getId() + "'" +
            ", metodoPago='" + getMetodoPago() + "'" +
            ", estado='" + getEstado() + "'" +
            ", monto=" + getMonto() +
            ", referenciaPasarela='" + getReferenciaPasarela() + "'" +
            ", codigoAutorizacion='" + getCodigoAutorizacion() + "'" +
            ", descripcionRespuesta='" + getDescripcionRespuesta() + "'" +
            ", intentos=" + getIntentos() +
            ", fechaPago='" + getFechaPago() + "'" +
            ", pedido=" + getPedido() +
            "}";
    }
}
