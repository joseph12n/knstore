package com.mycompany.knstore.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EnvioTrackingRequestDTO {

    @NotBlank
    @Size(max = 100)
    private String numeroRastreo;

    @Size(max = 100)
    private String transportadora;

    @Size(max = 300)
    private String urlRastreo;

    public String getNumeroRastreo() {
        return numeroRastreo;
    }

    public void setNumeroRastreo(String numeroRastreo) {
        this.numeroRastreo = numeroRastreo;
    }

    public String getTransportadora() {
        return transportadora;
    }

    public void setTransportadora(String transportadora) {
        this.transportadora = transportadora;
    }

    public String getUrlRastreo() {
        return urlRastreo;
    }

    public void setUrlRastreo(String urlRastreo) {
        this.urlRastreo = urlRastreo;
    }
}
