package com.mycompany.knstore.domain.enumeration;

/**
 * The EstadoPedido enumeration.
 */
public enum EstadoPedido {
    PENDING("Pendiente"),
    CONFIRMED("Confirmado"),
    PROCESSING("EnProceso"),
    SHIPPED("Enviado"),
    DELIVERED("Entregado"),
    CANCELLED("Cancelado"),
    RETURNED("Devuelto");

    private final String value;

    EstadoPedido(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
