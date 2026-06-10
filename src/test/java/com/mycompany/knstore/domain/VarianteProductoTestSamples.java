package com.mycompany.knstore.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class VarianteProductoTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static VarianteProducto getVarianteProductoSample1() {
        return new VarianteProducto()
            .id("id1")
            .sku("sku1")
            .color("color1")
            .talla("talla1")
            .codigoBarras("codigoBarras1")
            .stock(1)
            .stockMinimo(1)
            .ubicacionBodega("ubicacionBodega1");
    }

    public static VarianteProducto getVarianteProductoSample2() {
        return new VarianteProducto()
            .id("id2")
            .sku("sku2")
            .color("color2")
            .talla("talla2")
            .codigoBarras("codigoBarras2")
            .stock(2)
            .stockMinimo(2)
            .ubicacionBodega("ubicacionBodega2");
    }

    public static VarianteProducto getVarianteProductoRandomSampleGenerator() {
        return new VarianteProducto()
            .id(UUID.randomUUID().toString())
            .sku(UUID.randomUUID().toString())
            .color(UUID.randomUUID().toString())
            .talla(UUID.randomUUID().toString())
            .codigoBarras(UUID.randomUUID().toString())
            .stock(intCount.incrementAndGet())
            .stockMinimo(intCount.incrementAndGet())
            .ubicacionBodega(UUID.randomUUID().toString());
    }
}
