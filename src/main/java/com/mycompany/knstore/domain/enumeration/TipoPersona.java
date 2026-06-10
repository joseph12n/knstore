package com.mycompany.knstore.domain.enumeration;

/**
 * The TipoPersona enumeration.
 */
public enum TipoPersona {
    NATURAL("Natural"),
    JURIDICA("Juridica");

    private final String value;

    TipoPersona(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
