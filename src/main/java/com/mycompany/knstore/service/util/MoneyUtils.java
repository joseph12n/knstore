package com.mycompany.knstore.service.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utilidades de normalizacion monetaria: todo valor monetario se persiste con
 * exactamente 2 decimales usando redondeo HALF_UP (RNF-026).
 */
public final class MoneyUtils {

    public static final int SCALE = 2;

    public static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    private MoneyUtils() {}

    /**
     * Normaliza un valor monetario a 2 decimales con redondeo HALF_UP.
     *
     * @param valor valor monetario o {@code null}.
     * @return valor normalizado o {@code null}.
     */
    public static BigDecimal normalizar(BigDecimal valor) {
        if (valor == null) {
            return null;
        }
        return valor.setScale(SCALE, ROUNDING);
    }

    /**
     * Multiplica cantidad por precio normalizando el resultado.
     *
     * @param cantidad cantidad.
     * @param precio precio unitario.
     * @return resultado con 2 decimales.
     */
    public static BigDecimal multiplicar(BigDecimal cantidad, BigDecimal precio) {
        BigDecimal base = cantidad == null ? BigDecimal.ZERO : cantidad;
        BigDecimal precioBase = precio == null ? BigDecimal.ZERO : precio;
        return base.multiply(precioBase).setScale(SCALE, ROUNDING);
    }
}
