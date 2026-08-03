package com.mycompany.knstore.service.dto;

import com.mycompany.knstore.domain.enumeration.EstadoPedido;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PedidoEstadoUpdateRequestDTO {

    @NotNull
    private EstadoPedido estado;

    @Size(max = 500)
    private String motivo;

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
