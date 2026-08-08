package com.mycompany.knstore.service.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyUtilsTest {

    @Test
    void normalizarMantieneNulo() {
        assertThat(MoneyUtils.normalizar(null)).isNull();
    }

    @Test
    void normalizarRecortaA2DecimalesConHalfUp() {
        assertThat(MoneyUtils.normalizar(new BigDecimal("123.456"))).isEqualByComparingTo(new BigDecimal("123.46"));
        assertThat(MoneyUtils.normalizar(new BigDecimal("123.454"))).isEqualByComparingTo(new BigDecimal("123.45"));
        assertThat(MoneyUtils.normalizar(new BigDecimal("1000"))).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(MoneyUtils.normalizar(new BigDecimal("1000")).scale()).isEqualTo(2);
    }

    @Test
    void multiplicarNormalizaElResultado() {
        BigDecimal resultado = MoneyUtils.multiplicar(new BigDecimal("3"), new BigDecimal("120000.005"));
        assertThat(resultado).isEqualByComparingTo(new BigDecimal("360000.02"));
        assertThat(resultado.scale()).isEqualTo(2);
    }

    @Test
    void multiplicarConNulosDevuelveCero() {
        assertThat(MoneyUtils.multiplicar(null, new BigDecimal("100"))).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(MoneyUtils.multiplicar(new BigDecimal("2"), null)).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
