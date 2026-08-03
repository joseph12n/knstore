package com.mycompany.knstore.service.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.domain.ProductoPrecio;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductoPrecioUtilsTest {

    @Test
    void normalizeAndComputeGananciaUsesTwoDecimals() {
        ProductoPrecio precio = new ProductoPrecio();
        precio.setPrecioCompra(new BigDecimal("10.005"));
        precio.setPrecioVenta(new BigDecimal("15.019"));

        ProductoPrecioUtils.normalizeAndComputeGanancia(precio);

        assertThat(precio.getPrecioCompra()).isEqualByComparingTo("10.01");
        assertThat(precio.getPrecioVenta()).isEqualByComparingTo("15.02");
        assertThat(precio.getGanancia()).isEqualByComparingTo("5.01");
    }

    @Test
    void normalizeAndComputeGananciaKeepsExistingGananciaWhenPricesAreIncomplete() {
        ProductoPrecio precio = new ProductoPrecio();
        precio.setPrecioCompra(new BigDecimal("10.00"));
        precio.setGanancia(new BigDecimal("2.345"));

        ProductoPrecioUtils.normalizeAndComputeGanancia(precio);

        assertThat(precio.getGanancia()).isEqualByComparingTo("2.35");
    }
}
