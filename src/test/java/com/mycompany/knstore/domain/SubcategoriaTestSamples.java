package com.mycompany.knstore.domain;

import java.util.UUID;

public class SubcategoriaTestSamples {

    public static Subcategoria getSubcategoriaSample1() {
        return new Subcategoria().id("id1").nombre("nombre1").slug("slug1");
    }

    public static Subcategoria getSubcategoriaSample2() {
        return new Subcategoria().id("id2").nombre("nombre2").slug("slug2");
    }

    public static Subcategoria getSubcategoriaRandomSampleGenerator() {
        return new Subcategoria().id(UUID.randomUUID().toString()).nombre(UUID.randomUUID().toString()).slug(UUID.randomUUID().toString());
    }
}
