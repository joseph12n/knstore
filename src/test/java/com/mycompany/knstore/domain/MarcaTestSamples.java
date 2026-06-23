package com.mycompany.knstore.domain;

import java.util.UUID;

public class MarcaTestSamples {

    public static Marca getMarcaSample1() {
        return new Marca().id("id1").nombre("nombre1").slug("slug1");
    }

    public static Marca getMarcaSample2() {
        return new Marca().id("id2").nombre("nombre2").slug("slug2");
    }

    public static Marca getMarcaRandomSampleGenerator() {
        return new Marca().id(UUID.randomUUID().toString()).nombre(UUID.randomUUID().toString()).slug(UUID.randomUUID().toString());
    }
}
