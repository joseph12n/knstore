package com.mycompany.knstore.domain.enumeration;

/**
 * The TipoServicioEnvio enumeration.
 */
public enum TipoServicioEnvio {
    ESTANDAR("Estandar"),
    EXPRESS("Express"),
    MISMO_DIA("MismoDia"),
    PROGRAMADO("Programado"),
    PUNTO_PICKUP("PuntoPickup");

    private final String value;

    TipoServicioEnvio(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
