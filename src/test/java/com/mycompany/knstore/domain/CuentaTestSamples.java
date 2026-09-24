package com.mycompany.knstore.domain;

import java.util.UUID;

public class CuentaTestSamples {

    public static Cuenta getCuentaSample1() {
        return new Cuenta()
            .id("id1")
            .numDocumento("1234567890")
            .primerNombre("PrimerNombreUno")
            .segundoNombre("SegundoNombreUno")
            .primerApellido("PrimerApellidoUno")
            .segundoApellido("SegundoApellidoUno")
            .celular("3000000001")
            .telefono("6010000001");
    }

    public static Cuenta getCuentaSample2() {
        return new Cuenta()
            .id("id2")
            .numDocumento("2234567890")
            .primerNombre("PrimerNombreDos")
            .segundoNombre("SegundoNombreDos")
            .primerApellido("PrimerApellidoDos")
            .segundoApellido("SegundoApellidoDos")
            .celular("3000000002")
            .telefono("6010000002");
    }

    public static Cuenta getCuentaRandomSampleGenerator() {
        return new Cuenta()
            .id(UUID.randomUUID().toString())
            .numDocumento("1" + Integer.toUnsignedString(UUID.randomUUID().hashCode()))
            .primerNombre(randomLetters())
            .segundoNombre(randomLetters())
            .primerApellido(randomLetters())
            .segundoApellido(randomLetters())
            .celular("3000000000")
            .telefono("6010000000");
    }

    private static String randomLetters() {
        return UUID.randomUUID().toString().replaceAll("[^a-zA-Z]", "");
    }
}
