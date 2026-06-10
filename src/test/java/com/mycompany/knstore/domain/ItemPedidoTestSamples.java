package com.mycompany.knstore.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemPedidoTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ItemPedido getItemPedidoSample1() {
        return new ItemPedido().id("id1").cantidad(1);
    }

    public static ItemPedido getItemPedidoSample2() {
        return new ItemPedido().id("id2").cantidad(2);
    }

    public static ItemPedido getItemPedidoRandomSampleGenerator() {
        return new ItemPedido().id(UUID.randomUUID().toString()).cantidad(intCount.incrementAndGet());
    }
}
