package com.mycompany.knstore.service.dto;

import com.mycompany.knstore.domain.enumeration.EstadoEnvio;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EnvioEstadoUpdateRequestDTO {

    @NotNull
    private EstadoEnvio estado;

    @Size(max = 300)
    private String observacion;

    public EstadoEnvio getEstado() {
        return estado;
    }

    public void setEstado(EstadoEnvio estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
