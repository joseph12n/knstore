package com.mycompany.knstore.domain;

import java.util.UUID;

public class CategoriaIVATestSamples {

    public static CategoriaIVA getCategoriaIVASample1() {
        return new CategoriaIVA().id("id1").nombre("nombre1");
    }

    public static CategoriaIVA getCategoriaIVASample2() {
        return new CategoriaIVA().id("id2").nombre("nombre2");
    }

    public static CategoriaIVA getCategoriaIVARandomSampleGenerator() {
        return new CategoriaIVA().id(UUID.randomUUID().toString()).nombre(UUID.randomUUID().toString());
    }
}
