package com.mycompany.knstore.service.dto;

import com.mycompany.knstore.domain.enumeration.MetodoPago;
import com.mycompany.knstore.domain.enumeration.TipoServicioEnvio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Solicitud de checkout atómico para crear pedido, ítems, pago, envío y factura.
 */
public class CheckoutRequestDTO implements Serializable {

    @NotBlank
    private String direccionId;

    @NotNull
    private MetodoPago metodoPago;

    @NotNull
    private TipoServicioEnvio tipoServicioEnvio;

    private String notasCliente;

    @NotEmpty
    private List<CheckoutItemDTO> items;

    public String getDireccionId() {
        return direccionId;
    }

    public void setDireccionId(String direccionId) {
        this.direccionId = direccionId;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public TipoServicioEnvio getTipoServicioEnvio() {
        return tipoServicioEnvio;
    }

    public void setTipoServicioEnvio(TipoServicioEnvio tipoServicioEnvio) {
        this.tipoServicioEnvio = tipoServicioEnvio;
    }

    public String getNotasCliente() {
        return notasCliente;
    }

    public void setNotasCliente(String notasCliente) {
        this.notasCliente = notasCliente;
    }

    public List<CheckoutItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CheckoutItemDTO> items) {
        this.items = items;
    }
}
