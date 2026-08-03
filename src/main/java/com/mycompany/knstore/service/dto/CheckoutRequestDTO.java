package com.mycompany.knstore.service.dto;

import com.mycompany.knstore.domain.enumeration.TipoServicioEnvio;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

public class CheckoutRequestDTO implements Serializable {

    @NotBlank
    private String direccionId;

    @Valid
    @NotEmpty
    private List<CheckoutItemDTO> items;

    private TipoServicioEnvio tipoServicioEnvio;

    private String notasCliente;

    public String getDireccionId() {
        return direccionId;
    }

    public void setDireccionId(String direccionId) {
        this.direccionId = direccionId;
    }

    public List<CheckoutItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CheckoutItemDTO> items) {
        this.items = items;
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
}
