package com.mycompany.knstore.service.dto;

import com.mycompany.knstore.domain.enumeration.EstadoPedido;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.knstore.domain.Pedido} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PedidoDTO implements Serializable {

    private String id;

    @NotNull
    @Size(max = 30)
    private String numeroPedido;

    @NotNull
    private EstadoPedido estado;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal subtotal;

    @DecimalMin(value = "0")
    private BigDecimal descuento;

    @DecimalMin(value = "0")
    private BigDecimal ivaTotal;

    @DecimalMin(value = "0")
    private BigDecimal costoEnvio;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal total;

    @Size(max = 500)
    private String notasCliente;

    @Size(max = 500)
    private String notasInternas;

    @Size(max = 45)
    private String ipOrigen;

    @Size(max = 300)
    private String userAgent;

    @NotNull
    private DireccionDTO direccion;

    @NotNull
    private EnvioDTO envio;

    @NotNull
    private CuentaDTO cuenta;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(String numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getIvaTotal() {
        return ivaTotal;
    }

    public void setIvaTotal(BigDecimal ivaTotal) {
        this.ivaTotal = ivaTotal;
    }

    public BigDecimal getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(BigDecimal costoEnvio) {
        this.costoEnvio = costoEnvio;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getNotasCliente() {
        return notasCliente;
    }

    public void setNotasCliente(String notasCliente) {
        this.notasCliente = notasCliente;
    }

    public String getNotasInternas() {
        return notasInternas;
    }

    public void setNotasInternas(String notasInternas) {
        this.notasInternas = notasInternas;
    }

    public String getIpOrigen() {
        return ipOrigen;
    }

    public void setIpOrigen(String ipOrigen) {
        this.ipOrigen = ipOrigen;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public DireccionDTO getDireccion() {
        return direccion;
    }

    public void setDireccion(DireccionDTO direccion) {
        this.direccion = direccion;
    }

    public EnvioDTO getEnvio() {
        return envio;
    }

    public void setEnvio(EnvioDTO envio) {
        this.envio = envio;
    }

    public CuentaDTO getCuenta() {
        return cuenta;
    }

    public void setCuenta(CuentaDTO cuenta) {
        this.cuenta = cuenta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PedidoDTO)) {
            return false;
        }

        PedidoDTO pedidoDTO = (PedidoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, pedidoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PedidoDTO{" +
            "id='" + getId() + "'" +
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
            ", direccion=" + getDireccion() +
            ", envio=" + getEnvio() +
            ", cuenta=" + getCuenta() +
            "}";
    }
}
