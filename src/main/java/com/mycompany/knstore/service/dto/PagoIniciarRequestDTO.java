package com.mycompany.knstore.service.dto;

import com.mycompany.knstore.domain.enumeration.MetodoPago;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public class PagoIniciarRequestDTO implements Serializable {

    @NotBlank
    private String pedidoId;

    @NotNull
    private MetodoPago metodoPago;

    public String getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(String pedidoId) {
        this.pedidoId = pedidoId;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }
}
