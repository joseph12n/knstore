package com.mycompany.knstore.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemCarritoTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ItemCarrito getItemCarritoSample1() {
        return new ItemCarrito().id("id1").cantidad(1);
    }

    public static ItemCarrito getItemCarritoSample2() {
        return new ItemCarrito().id("id2").cantidad(2);
    }

    public static ItemCarrito getItemCarritoRandomSampleGenerator() {
        return new ItemCarrito().id(UUID.randomUUID().toString()).cantidad(intCount.incrementAndGet());
    }
}
