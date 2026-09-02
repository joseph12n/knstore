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

    /**
     * Valida la maquina de estados del pedido:
     * PENDING » CONFIRMED » SHIPPED » DELIVERED (» RETURNED), PROCESSING » SHIPPED,
     * y CANCELLED solo desde PENDING, CONFIRMED o PROCESSING. Los estados terminales
     * (CANCELLED, RETURNED) no tienen transiciones validas.
     *
     * @param nuevo estado destino.
     * @return {@code true} si la transicion desde este estado es valida.
     */
    public boolean puedeTransicionarA(EstadoPedido nuevo) {
        if (nuevo == null) {
            return false;
        }
        if (this == nuevo) {
            return true;
        }
        return switch (this) {
            case PENDING -> nuevo == CONFIRMED || nuevo == CANCELLED;
            case CONFIRMED -> nuevo == SHIPPED || nuevo == CANCELLED;
            case SHIPPED -> nuevo == DELIVERED;
            case DELIVERED -> nuevo == RETURNED;
            case PROCESSING -> nuevo == CANCELLED || nuevo == SHIPPED;
            case RETURNED, CANCELLED -> false;
        };
    }
}
