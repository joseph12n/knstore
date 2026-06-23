package com.mycompany.knstore.domain;

import java.util.UUID;

public class FacturaTestSamples {

    public static Factura getFacturaSample1() {
        return new Factura().id("id1").prefijo("prefijo1").cufe("cufe1").notasAdicionales("notasAdicionales1");
    }

    public static Factura getFacturaSample2() {
        return new Factura().id("id2").prefijo("prefijo2").cufe("cufe2").notasAdicionales("notasAdicionales2");
    }

    public static Factura getFacturaRandomSampleGenerator() {
        return new Factura()
            .id(UUID.randomUUID().toString())
            .prefijo(UUID.randomUUID().toString())
            .cufe(UUID.randomUUID().toString())
            .notasAdicionales(UUID.randomUUID().toString());
    }
}
