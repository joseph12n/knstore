package com.mycompany.knstore.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class FacturaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Factura getFacturaSample1() {
        return new Factura()
            .id("id1")
            .referencia("referencia1")
            .cufe("cufe1")
            .resolucionDian("resolucionDian1")
            .prefijo("prefijo1")
            .consecutivo(1L)
            .notasAdicionales("notasAdicionales1");
    }

    public static Factura getFacturaSample2() {
        return new Factura()
            .id("id2")
            .referencia("referencia2")
            .cufe("cufe2")
            .resolucionDian("resolucionDian2")
            .prefijo("prefijo2")
            .consecutivo(2L)
            .notasAdicionales("notasAdicionales2");
    }

    public static Factura getFacturaRandomSampleGenerator() {
        return new Factura()
            .id(UUID.randomUUID().toString())
            .referencia(UUID.randomUUID().toString())
            .cufe(UUID.randomUUID().toString())
            .resolucionDian(UUID.randomUUID().toString())
            .prefijo(UUID.randomUUID().toString())
            .consecutivo(longCount.incrementAndGet())
            .notasAdicionales(UUID.randomUUID().toString());
    }
}
