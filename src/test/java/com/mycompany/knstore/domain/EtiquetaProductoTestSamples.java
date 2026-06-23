package com.mycompany.knstore.domain;

import java.util.UUID;

public class EtiquetaProductoTestSamples {

    public static EtiquetaProducto getEtiquetaProductoSample1() {
        return new EtiquetaProducto().id("id1").etiqueta("etiqueta1");
    }

    public static EtiquetaProducto getEtiquetaProductoSample2() {
        return new EtiquetaProducto().id("id2").etiqueta("etiqueta2");
    }

    public static EtiquetaProducto getEtiquetaProductoRandomSampleGenerator() {
        return new EtiquetaProducto().id(UUID.randomUUID().toString()).etiqueta(UUID.randomUUID().toString());
    }
}
