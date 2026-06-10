package com.mycompany.knstore.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class PagoTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Pago getPagoSample1() {
        return new Pago()
            .id("id1")
            .referenciaPasarela("referenciaPasarela1")
            .codigoAutorizacion("codigoAutorizacion1")
            .descripcionRespuesta("descripcionRespuesta1")
            .intentos(1);
    }

    public static Pago getPagoSample2() {
        return new Pago()
            .id("id2")
            .referenciaPasarela("referenciaPasarela2")
            .codigoAutorizacion("codigoAutorizacion2")
            .descripcionRespuesta("descripcionRespuesta2")
            .intentos(2);
    }

    public static Pago getPagoRandomSampleGenerator() {
        return new Pago()
            .id(UUID.randomUUID().toString())
            .referenciaPasarela(UUID.randomUUID().toString())
            .codigoAutorizacion(UUID.randomUUID().toString())
            .descripcionRespuesta(UUID.randomUUID().toString())
            .intentos(intCount.incrementAndGet());
    }
}
