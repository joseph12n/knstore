package com.mycompany.knstore.domain;

import java.util.UUID;

public class PedidoTestSamples {

    public static Pedido getPedidoSample1() {
        return new Pedido()
            .id("id1")
            .numeroPedido("numeroPedido1")
            .notasCliente("notasCliente1")
            .notasInternas("notasInternas1")
            .ipOrigen("ipOrigen1")
            .userAgent("userAgent1");
    }

    public static Pedido getPedidoSample2() {
        return new Pedido()
            .id("id2")
            .numeroPedido("numeroPedido2")
            .notasCliente("notasCliente2")
            .notasInternas("notasInternas2")
            .ipOrigen("ipOrigen2")
            .userAgent("userAgent2");
    }

    public static Pedido getPedidoRandomSampleGenerator() {
        return new Pedido()
            .id(UUID.randomUUID().toString())
            .numeroPedido(UUID.randomUUID().toString())
            .notasCliente(UUID.randomUUID().toString())
            .notasInternas(UUID.randomUUID().toString())
            .ipOrigen(UUID.randomUUID().toString())
            .userAgent(UUID.randomUUID().toString());
    }
}
