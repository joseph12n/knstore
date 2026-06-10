package com.mycompany.knstore.domain.enumeration;

/**
 * The EstadoTipoDocumento enumeration.
 */
public enum EstadoTipoDocumento {
    ACTIVO("Activo"),
    INACTIVO("Inactivo");

    private final String value;

    EstadoTipoDocumento(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
