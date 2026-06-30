package com.mycompany.knstore.domain.enumeration;

/**
 * The EstadoEnvio enumeration.
 */
public enum EstadoEnvio {
    PENDING("Pendiente"),
    DISPATCHED("Despachado"),
    IN_TRANSIT("EnTransito"),
    IN_CITY("EnCiudad"),
    DELIVERED("Entregado"),
    RETURNED("Devuelto"),
    LOST("Perdido");

    private final String value;

    EstadoEnvio(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
