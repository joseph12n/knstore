package com.mycompany.knstore.domain.enumeration;

/**
 * The MetodoPago enumeration.
 */
public enum MetodoPago {
    CREDIT_CARD("TarjetaCredito"),
    DEBIT_CARD("TarjetaDebito"),
    PSE,
    CASH("Efectivo"),
    NEQUI("Nequi"),
    DAVIPLATA("Daviplata"),
    EFECTY("Efecty"),
    CONTRA_ENTREGA("ContraEntrega");

    private String value;

    MetodoPago() {}

    MetodoPago(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
