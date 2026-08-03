package com.mycompany.knstore.service.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyUtilsTest {

    @Test
    void normalizeRoundsHalfUpToTwoDecimals() {
        assertThat(MoneyUtils.normalize(new BigDecimal("10.125"))).isEqualByComparingTo(new BigDecimal("10.13"));
        assertThat(MoneyUtils.normalize(new BigDecimal("10.124"))).isEqualByComparingTo(new BigDecimal("10.12"));
    }

    @Test
    void normalizeReturnsNullForNullInput() {
        assertThat(MoneyUtils.normalize(null)).isNull();
    }

    @Test
    void normalizeOrZeroReturnsZeroWhenNull() {
        assertThat(MoneyUtils.normalizeOrZero(null)).isEqualByComparingTo(new BigDecimal("0.00"));
    }
}
