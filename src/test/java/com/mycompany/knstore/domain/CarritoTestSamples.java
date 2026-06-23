package com.mycompany.knstore.domain;

import java.util.UUID;

public class CarritoTestSamples {

    public static Carrito getCarritoSample1() {
        return new Carrito().id("id1");
    }

    public static Carrito getCarritoSample2() {
        return new Carrito().id("id2");
    }

    public static Carrito getCarritoRandomSampleGenerator() {
        return new Carrito().id(UUID.randomUUID().toString());
    }
}
