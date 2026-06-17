package com.mycompany.knstore.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemPedidoTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ItemPedido getItemPedidoSample1() {
        return new ItemPedido()
            .id("id1")
            .nombreProducto("nombreProducto1")
            .slugProducto("slugProducto1")
            .marcaProducto("marcaProducto1")
            .skuProducto("skuProducto1")
            .colorProducto("colorProducto1")
            .tallaProducto("tallaProducto1")
            .cantidad(1);
    }

    public static ItemPedido getItemPedidoSample2() {
        return new ItemPedido()
            .id("id2")
            .nombreProducto("nombreProducto2")
            .slugProducto("slugProducto2")
            .marcaProducto("marcaProducto2")
            .skuProducto("skuProducto2")
            .colorProducto("colorProducto2")
            .tallaProducto("tallaProducto2")
            .cantidad(2);
    }

    public static ItemPedido getItemPedidoRandomSampleGenerator() {
        return new ItemPedido()
            .id(UUID.randomUUID().toString())
            .nombreProducto(UUID.randomUUID().toString())
            .slugProducto(UUID.randomUUID().toString())
            .marcaProducto(UUID.randomUUID().toString())
            .skuProducto(UUID.randomUUID().toString())
            .colorProducto(UUID.randomUUID().toString())
            .tallaProducto(UUID.randomUUID().toString())
            .cantidad(intCount.incrementAndGet());
    }
}
