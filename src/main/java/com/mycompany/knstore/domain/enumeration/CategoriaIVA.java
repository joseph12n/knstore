package com.mycompany.knstore.domain.enumeration;

/**
 * The CategoriaIVA enumeration.
 */
public enum CategoriaIVA {
    EXCLUIDO("Excluido"),
    EXENTO("Exento"),
    GRAVADO_5("Gravado5"),
    GRAVADO_19("Gravado19");

    private final String value;

    CategoriaIVA(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
