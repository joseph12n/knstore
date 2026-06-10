package com.mycompany.knstore.domain;

import java.util.UUID;

public class CuentaTestSamples {

    public static Cuenta getCuentaSample1() {
        return new Cuenta()
            .id("id1")
            .numDocumento("numDocumento1")
            .primerNombre("primerNombre1")
            .segundoNombre("segundoNombre1")
            .primerApellido("primerApellido1")
            .segundoApellido("segundoApellido1")
            .celular("celular1")
            .telefono("telefono1");
    }

    public static Cuenta getCuentaSample2() {
        return new Cuenta()
            .id("id2")
            .numDocumento("numDocumento2")
            .primerNombre("primerNombre2")
            .segundoNombre("segundoNombre2")
            .primerApellido("primerApellido2")
            .segundoApellido("segundoApellido2")
            .celular("celular2")
            .telefono("telefono2");
    }

    public static Cuenta getCuentaRandomSampleGenerator() {
        return new Cuenta()
            .id(UUID.randomUUID().toString())
            .numDocumento(UUID.randomUUID().toString())
            .primerNombre(UUID.randomUUID().toString())
            .segundoNombre(UUID.randomUUID().toString())
            .primerApellido(UUID.randomUUID().toString())
            .segundoApellido(UUID.randomUUID().toString())
            .celular(UUID.randomUUID().toString())
            .telefono(UUID.randomUUID().toString());
    }
}
