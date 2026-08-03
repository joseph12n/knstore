package com.mycompany.knstore.domain;

import java.util.UUID;

public class DireccionTestSamples {

    public static Direccion getDireccionSample1() {
        return new Direccion()
            .id("id1")
            .direccion("direccion1")
            .barrio("barrio1")
            .localidad("localidad1")
            .municipio("municipio1")
            .departamento("departamento1")
            .telefonoContacto("telefonoContacto1")
            .destinatario("destinatario1")
            .codigoPostal("codigoPostal1");
    }

    public static Direccion getDireccionSample2() {
        return new Direccion()
            .id("id2")
            .direccion("direccion2")
            .barrio("barrio2")
            .localidad("localidad2")
            .municipio("municipio2")
            .departamento("departamento2")
            .telefonoContacto("telefonoContacto2")
            .destinatario("destinatario2")
            .codigoPostal("codigoPostal2");
    }

    public static Direccion getDireccionRandomSampleGenerator() {
        return new Direccion()
            .id(UUID.randomUUID().toString())
            .direccion(UUID.randomUUID().toString())
            .barrio(UUID.randomUUID().toString())
            .localidad(UUID.randomUUID().toString())
            .municipio(UUID.randomUUID().toString())
            .departamento(UUID.randomUUID().toString())
            .telefonoContacto(UUID.randomUUID().toString())
            .destinatario(UUID.randomUUID().toString())
            .codigoPostal(UUID.randomUUID().toString());
    }
}
