package com.mycompany.knstore.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductoInventarioTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ProductoInventario getProductoInventarioSample1() {
        return new ProductoInventario().id("id1").stock(1).stockMinimo(1).garantiaMeses(1);
    }

    public static ProductoInventario getProductoInventarioSample2() {
        return new ProductoInventario().id("id2").stock(2).stockMinimo(2).garantiaMeses(2);
    }

    public static ProductoInventario getProductoInventarioRandomSampleGenerator() {
        return new ProductoInventario()
            .id(UUID.randomUUID().toString())
            .stock(intCount.incrementAndGet())
            .stockMinimo(intCount.incrementAndGet())
            .garantiaMeses(intCount.incrementAndGet());
    }
}
