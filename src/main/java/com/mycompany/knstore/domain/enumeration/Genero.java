package com.mycompany.knstore.domain.enumeration;

/**
 * The Genero enumeration.
 */
public enum Genero {
    MASCULINO("Masculino"),
    FEMENINO("Femenino"),
    NO_BINARIO("NoBinario"),
    PREFIERO_NO_DECIR("PrefieroNoDecir");

    private final String value;

    Genero(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
