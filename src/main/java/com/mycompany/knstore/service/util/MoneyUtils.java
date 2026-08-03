package com.mycompany.knstore.service.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MoneyUtils {

    private static final int SCALE = 2;

    private MoneyUtils() {}

    public static BigDecimal normalize(BigDecimal value) {
        return value == null ? null : value.setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal normalizeOrZero(BigDecimal value) {
        return normalize(value == null ? BigDecimal.ZERO : value);
    }
}
