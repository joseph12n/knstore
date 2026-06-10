package com.mycompany.knstore.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductoTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Producto getProductoSample1() {
        return new Producto()
            .id("id1")
            .nombre("nombre1")
            .slug("slug1")
            .imagenAlt("imagenAlt1")
            .marca("marca1")
            .referencia("referencia1")
            .codigoBarras("codigoBarras1")
            .unidadMedida("unidadMedida1")
            .garantiaMeses(1);
    }

    public static Producto getProductoSample2() {
        return new Producto()
            .id("id2")
            .nombre("nombre2")
            .slug("slug2")
            .imagenAlt("imagenAlt2")
            .marca("marca2")
            .referencia("referencia2")
            .codigoBarras("codigoBarras2")
            .unidadMedida("unidadMedida2")
            .garantiaMeses(2);
    }

    public static Producto getProductoRandomSampleGenerator() {
        return new Producto()
            .id(UUID.randomUUID().toString())
            .nombre(UUID.randomUUID().toString())
            .slug(UUID.randomUUID().toString())
            .imagenAlt(UUID.randomUUID().toString())
            .marca(UUID.randomUUID().toString())
            .referencia(UUID.randomUUID().toString())
            .codigoBarras(UUID.randomUUID().toString())
            .unidadMedida(UUID.randomUUID().toString())
            .garantiaMeses(intCount.incrementAndGet());
    }
}
