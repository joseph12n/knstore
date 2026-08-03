package com.mycompany.knstore.service.dto;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

public class PagoReembolsoRequestDTO implements Serializable {

    @NotBlank
    private String motivo;

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
