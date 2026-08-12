package com.mycompany.knstore.domain;

import java.util.UUID;

public class DireccionTestSamples {

    public static Direccion getDireccionSample1() {
        return new Direccion()
            .id("id1")
            .direccion("direccion1")
            .barrio("BarrioUno")
            .localidad("LocalidadUno")
            .municipio("MunicipioUno")
            .departamento("DepartamentoUno")
            .telefonoContacto("3001112233")
            .destinatario("DestinatarioUno")
            .codigoPostal("110111");
    }

    public static Direccion getDireccionSample2() {
        return new Direccion()
            .id("id2")
            .direccion("direccion2")
            .barrio("BarrioDos")
            .localidad("LocalidadDos")
            .municipio("MunicipioDos")
            .departamento("DepartamentoDos")
            .telefonoContacto("3004445566")
            .destinatario("DestinatarioDos")
            .codigoPostal("110222");
    }

    public static Direccion getDireccionRandomSampleGenerator() {
        return new Direccion()
            .id(UUID.randomUUID().toString())
            .direccion(UUID.randomUUID().toString())
            .barrio(randomLetters())
            .localidad(randomLetters())
            .municipio(randomLetters())
            .departamento(randomLetters())
            .telefonoContacto("3007778899")
            .destinatario(randomLetters())
            .codigoPostal("110333");
    }

    private static String randomLetters() {
        return UUID.randomUUID().toString().replaceAll("[^a-zA-Z]", "");
    }
}
