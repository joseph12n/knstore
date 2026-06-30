package com.mycompany.knstore.domain.enumeration;

/**
 * The EstadoPago enumeration.
 */
public enum EstadoPago {
    PENDING("Pendiente"),
    APPROVED("Aprobado"),
    REJECTED("Rechazado"),
    REFUNDED("Reembolsado"),
    EXPIRED("Expirado"),
    CANCELLED("Cancelado");

    private final String value;

    EstadoPago(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
