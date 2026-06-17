package com.mycompany.knstore.domain;

import java.util.UUID;

public class ProductoImagenTestSamples {

    public static ProductoImagen getProductoImagenSample1() {
        return new ProductoImagen().id("id1").imagenAlt("imagenAlt1");
    }

    public static ProductoImagen getProductoImagenSample2() {
        return new ProductoImagen().id("id2").imagenAlt("imagenAlt2");
    }

    public static ProductoImagen getProductoImagenRandomSampleGenerator() {
        return new ProductoImagen().id(UUID.randomUUID().toString()).imagenAlt(UUID.randomUUID().toString());
    }
}
