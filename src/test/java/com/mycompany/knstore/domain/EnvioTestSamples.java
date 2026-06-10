package com.mycompany.knstore.domain;

import java.util.UUID;

public class EnvioTestSamples {

    public static Envio getEnvioSample1() {
        return new Envio()
            .id("id1")
            .numeroRastreo("numeroRastreo1")
            .transportadora("transportadora1")
            .urlRastreo("urlRastreo1")
            .observaciones("observaciones1");
    }

    public static Envio getEnvioSample2() {
        return new Envio()
            .id("id2")
            .numeroRastreo("numeroRastreo2")
            .transportadora("transportadora2")
            .urlRastreo("urlRastreo2")
            .observaciones("observaciones2");
    }

    public static Envio getEnvioRandomSampleGenerator() {
        return new Envio()
            .id(UUID.randomUUID().toString())
            .numeroRastreo(UUID.randomUUID().toString())
            .transportadora(UUID.randomUUID().toString())
            .urlRastreo(UUID.randomUUID().toString())
            .observaciones(UUID.randomUUID().toString());
    }
}
