package com.mycompany.knstore.domain;

import static com.mycompany.knstore.domain.CategoriaTestSamples.*;
import static com.mycompany.knstore.domain.SubcategoriaTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SubcategoriaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Subcategoria.class);
        Subcategoria subcategoria1 = getSubcategoriaSample1();
        Subcategoria subcategoria2 = new Subcategoria();
        assertThat(subcategoria1).isNotEqualTo(subcategoria2);

        subcategoria2.setId(subcategoria1.getId());
        assertThat(subcategoria1).isEqualTo(subcategoria2);

        subcategoria2 = getSubcategoriaSample2();
        assertThat(subcategoria1).isNotEqualTo(subcategoria2);
    }

    @Test
    void categoriaTest() {
        Subcategoria subcategoria = getSubcategoriaRandomSampleGenerator();
        Categoria categoriaBack = getCategoriaRandomSampleGenerator();

        subcategoria.setCategoria(categoriaBack);
        assertThat(subcategoria.getCategoria()).isEqualTo(categoriaBack);

        subcategoria.categoria(null);
        assertThat(subcategoria.getCategoria()).isNull();
    }
}
