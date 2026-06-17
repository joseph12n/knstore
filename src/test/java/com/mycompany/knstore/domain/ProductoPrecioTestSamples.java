package com.mycompany.knstore.domain;

import java.util.UUID;

public class ProductoPrecioTestSamples {

    public static ProductoPrecio getProductoPrecioSample1() {
        return new ProductoPrecio().id("id1");
    }

    public static ProductoPrecio getProductoPrecioSample2() {
        return new ProductoPrecio().id("id2");
    }

    public static ProductoPrecio getProductoPrecioRandomSampleGenerator() {
        return new ProductoPrecio().id(UUID.randomUUID().toString());
    }
}
