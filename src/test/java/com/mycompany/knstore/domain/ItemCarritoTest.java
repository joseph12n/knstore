package com.mycompany.knstore.domain;

import static com.mycompany.knstore.domain.CarritoTestSamples.*;
import static com.mycompany.knstore.domain.ItemCarritoTestSamples.*;
import static com.mycompany.knstore.domain.ProductoTestSamples.*;
import static com.mycompany.knstore.domain.VarianteProductoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ItemCarritoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ItemCarrito.class);
        ItemCarrito itemCarrito1 = getItemCarritoSample1();
        ItemCarrito itemCarrito2 = new ItemCarrito();
        assertThat(itemCarrito1).isNotEqualTo(itemCarrito2);

        itemCarrito2.setId(itemCarrito1.getId());
        assertThat(itemCarrito1).isEqualTo(itemCarrito2);

        itemCarrito2 = getItemCarritoSample2();
        assertThat(itemCarrito1).isNotEqualTo(itemCarrito2);
    }

    @Test
    void carritoTest() {
        ItemCarrito itemCarrito = getItemCarritoRandomSampleGenerator();
        Carrito carritoBack = getCarritoRandomSampleGenerator();

        itemCarrito.setCarrito(carritoBack);
        assertThat(itemCarrito.getCarrito()).isEqualTo(carritoBack);

        itemCarrito.carrito(null);
        assertThat(itemCarrito.getCarrito()).isNull();
    }

    @Test
    void productoTest() {
        ItemCarrito itemCarrito = getItemCarritoRandomSampleGenerator();
        Producto productoBack = getProductoRandomSampleGenerator();

        itemCarrito.setProducto(productoBack);
        assertThat(itemCarrito.getProducto()).isEqualTo(productoBack);

        itemCarrito.producto(null);
        assertThat(itemCarrito.getProducto()).isNull();
    }

    @Test
    void varianteTest() {
        ItemCarrito itemCarrito = getItemCarritoRandomSampleGenerator();
        VarianteProducto varianteProductoBack = getVarianteProductoRandomSampleGenerator();

        itemCarrito.setVariante(varianteProductoBack);
        assertThat(itemCarrito.getVariante()).isEqualTo(varianteProductoBack);

        itemCarrito.variante(null);
        assertThat(itemCarrito.getVariante()).isNull();
    }
}
