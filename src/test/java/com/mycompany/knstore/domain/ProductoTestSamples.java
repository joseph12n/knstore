package com.mycompany.knstore.domain;

import java.util.UUID;

public class ProductoTestSamples {

    public static Producto getProductoSample1() {
        return new Producto()
            .id("id1")
            .nombre("nombre1")
            .slug("slug1")
            .referencia("referencia1")
            .sku("sku1")
            .color("color1")
            .talla("talla1")
            .codigoBarras("codigoBarras1")
            .unidadMedida("unidadMedida1");
    }

    public static Producto getProductoSample2() {
        return new Producto()
            .id("id2")
            .nombre("nombre2")
            .slug("slug2")
            .referencia("referencia2")
            .sku("sku2")
            .color("color2")
            .talla("talla2")
            .codigoBarras("codigoBarras2")
            .unidadMedida("unidadMedida2");
    }

    public static Producto getProductoRandomSampleGenerator() {
        return new Producto()
            .id(UUID.randomUUID().toString())
            .nombre(UUID.randomUUID().toString())
            .slug(UUID.randomUUID().toString())
            .referencia(UUID.randomUUID().toString())
            .sku(UUID.randomUUID().toString())
            .color(UUID.randomUUID().toString())
            .talla(UUID.randomUUID().toString())
            .codigoBarras(UUID.randomUUID().toString())
            .unidadMedida(UUID.randomUUID().toString());
    }
}
