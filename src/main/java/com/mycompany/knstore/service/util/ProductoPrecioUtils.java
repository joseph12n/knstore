package com.mycompany.knstore.service.util;

import com.mycompany.knstore.domain.ProductoPrecio;
import java.math.BigDecimal;

public final class ProductoPrecioUtils {

    private ProductoPrecioUtils() {}

    public static void normalizeAndComputeGanancia(ProductoPrecio productoPrecio) {
        if (productoPrecio == null) {
            return;
        }

        productoPrecio.setPrecioCompra(MoneyUtils.normalize(productoPrecio.getPrecioCompra()));
        productoPrecio.setPrecioVenta(MoneyUtils.normalize(productoPrecio.getPrecioVenta()));
        productoPrecio.setPrecioAdicional(MoneyUtils.normalize(productoPrecio.getPrecioAdicional()));

        BigDecimal precioCompra = productoPrecio.getPrecioCompra();
        BigDecimal precioVenta = productoPrecio.getPrecioVenta();
        if (precioCompra != null && precioVenta != null) {
            productoPrecio.setGanancia(MoneyUtils.normalize(precioVenta.subtract(precioCompra)));
            return;
        }

        productoPrecio.setGanancia(MoneyUtils.normalize(productoPrecio.getGanancia()));
    }
}
