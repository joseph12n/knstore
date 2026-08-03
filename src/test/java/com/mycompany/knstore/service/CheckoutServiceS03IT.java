package com.mycompany.knstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mycompany.knstore.IntegrationTest;
import com.mycompany.knstore.domain.CategoriaIVA;
import com.mycompany.knstore.domain.Cuenta;
import com.mycompany.knstore.domain.Direccion;
import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.domain.ProductoInventario;
import com.mycompany.knstore.domain.ProductoPrecio;
import com.mycompany.knstore.domain.enumeration.EstadoIVA;
import com.mycompany.knstore.domain.enumeration.MetodoPago;
import com.mycompany.knstore.domain.enumeration.TipoServicioEnvio;
import com.mycompany.knstore.repository.CarritoRepository;
import com.mycompany.knstore.repository.DireccionRepository;
import com.mycompany.knstore.repository.EnvioRepository;
import com.mycompany.knstore.repository.ItemCarritoRepository;
import com.mycompany.knstore.repository.ItemPedidoRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.repository.ProductoInventarioRepository;
import com.mycompany.knstore.repository.ProductoRepository;
import com.mycompany.knstore.service.dto.CheckoutItemDTO;
import com.mycompany.knstore.service.dto.CheckoutRequestDTO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class CheckoutServiceS03IT {

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ProductoInventarioRepository productoInventarioRepository;

    @Autowired
    private DireccionRepository direccionRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ItemCarritoRepository itemCarritoRepository;

    @BeforeEach
    void setUp() {
        cleanupData();
    }

    @AfterEach
    void tearDown() {
        cleanupData();
    }

    @Test
    void rollbackCuandoFallaDecrementoIntermedio() {
        Cuenta cuenta = buildCuenta("cuenta-s03-1");
        Direccion direccion = saveDireccion(cuenta, "dir-s03-1");

        ProductoInventario invOk = saveInventario("inv-ok", 10);
        Producto productoOk = saveProducto("prod-ok", invOk, new BigDecimal("10000"));

        ProductoInventario invMissingRef = new ProductoInventario();
        invMissingRef.setId("inv-missing");
        invMissingRef.setStock(10);
        Producto productoFail = saveProducto("prod-fail", invMissingRef, new BigDecimal("15000"));

        long pedidosAntes = pedidoRepository.count();
        long itemsAntes = itemPedidoRepository.count();
        long enviosAntes = envioRepository.count();

        CheckoutRequestDTO request = buildRequest(
            direccion.getId(),
            List.of(buildItem(productoOk.getId(), 1, new BigDecimal("10000")), buildItem(productoFail.getId(), 1, new BigDecimal("15000")))
        );

        assertThatThrownBy(() -> checkoutService.checkout(cuenta, request))
            .isInstanceOf(CheckoutException.class)
            .hasMessageContaining("posible concurrencia");

        assertThat(pedidoRepository.count()).isEqualTo(pedidosAntes);
        assertThat(itemPedidoRepository.count()).isEqualTo(itemsAntes);
        assertThat(envioRepository.count()).isEqualTo(enviosAntes);

        ProductoInventario inventarioActualizado = productoInventarioRepository.findById("inv-ok").orElseThrow();
        assertThat(inventarioActualizado.getStock()).isEqualTo(10);
    }

    @Test
    void concurrenciaSobreMismoStockPermiteSoloUnCheckout() throws Exception {
        Cuenta cuenta = buildCuenta("cuenta-s03-2");
        Direccion direccion = saveDireccion(cuenta, "dir-s03-2");

        ProductoInventario inventario = saveInventario("inv-concurrent", 1);
        Producto producto = saveProducto("prod-concurrent", inventario, new BigDecimal("12000"));

        CheckoutRequestDTO request = buildRequest(direccion.getId(), List.of(buildItem(producto.getId(), 1, new BigDecimal("12000"))));

        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Callable<Boolean> task = () -> {
                try {
                    checkoutService.checkout(cuenta, request);
                    return true;
                } catch (RuntimeException ex) {
                    return false;
                }
            };

            List<Future<Boolean>> futures = executor.invokeAll(List.of(task, task));
            List<Boolean> results = new ArrayList<>();
            for (Future<Boolean> future : futures) {
                results.add(future.get());
            }

            long successCount = results.stream().filter(Boolean::booleanValue).count();
            long failCount = results.size() - successCount;

            assertThat(successCount).isEqualTo(1);
            assertThat(failCount).isEqualTo(1);
            assertThat(pedidoRepository.count()).isEqualTo(1);
            assertThat(itemPedidoRepository.count()).isEqualTo(1);
            assertThat(envioRepository.count()).isEqualTo(1);

            ProductoInventario inventarioActualizado = productoInventarioRepository.findById("inv-concurrent").orElseThrow();
            assertThat(inventarioActualizado.getStock()).isEqualTo(0);
        } finally {
            executor.shutdownNow();
        }
    }

    private void cleanupData() {
        itemCarritoRepository.deleteAll();
        carritoRepository.deleteAll();
        itemPedidoRepository.deleteAll();
        envioRepository.deleteAll();
        pedidoRepository.deleteAll();
        productoRepository.deleteAll();
        productoInventarioRepository.deleteAll();
        direccionRepository.deleteAll();
    }

    private Cuenta buildCuenta(String id) {
        Cuenta cuenta = new Cuenta();
        cuenta.setId(id);
        cuenta.setPrimerNombre("Test");
        cuenta.setPrimerApellido("S03");
        cuenta.setActivo(true);
        return cuenta;
    }

    private Direccion saveDireccion(Cuenta cuenta, String id) {
        Direccion direccion = new Direccion();
        direccion.setId(id);
        direccion.setDireccion("Calle 123 #45-67");
        direccion.setMunicipio("Bogota");
        direccion.setDepartamento("Cundinamarca");
        direccion.setActivo(true);
        direccion.setCuenta(cuenta);
        return direccionRepository.save(direccion);
    }

    private ProductoInventario saveInventario(String id, int stock) {
        ProductoInventario inventario = new ProductoInventario();
        inventario.setId(id);
        inventario.setStock(stock);
        return productoInventarioRepository.save(inventario);
    }

    private Producto saveProducto(String id, ProductoInventario inventario, BigDecimal precioVenta) {
        ProductoPrecio precio = new ProductoPrecio();
        precio.setPrecioCompra(precioVenta.subtract(BigDecimal.valueOf(1000)));
        precio.setPrecioVenta(precioVenta);

        CategoriaIVA categoriaIVA = new CategoriaIVA();
        categoriaIVA.setId("iva-19");
        categoriaIVA.setNombre("General");
        categoriaIVA.setPorcentaje(BigDecimal.valueOf(19));
        categoriaIVA.setEstado(EstadoIVA.ACTIVO);

        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre("Producto " + id);
        producto.setSlug("producto-" + id);
        producto.setSku("SKU-" + id);
        producto.setDestacado(false);
        producto.setActivo(true);
        producto.setPrecio(precio);
        producto.setInventario(inventario);
        producto.setCategoriaIva(categoriaIVA);
        return productoRepository.save(producto);
    }

    private CheckoutItemDTO buildItem(String productoId, int cantidad, BigDecimal precioUnitario) {
        CheckoutItemDTO item = new CheckoutItemDTO();
        item.setProductoId(productoId);
        item.setCantidad(cantidad);
        item.setPrecioUnitario(precioUnitario);
        return item;
    }

    private CheckoutRequestDTO buildRequest(String direccionId, List<CheckoutItemDTO> items) {
        CheckoutRequestDTO request = new CheckoutRequestDTO();
        request.setDireccionId(direccionId);
        request.setMetodoPago(MetodoPago.NEQUI);
        request.setTipoServicioEnvio(TipoServicioEnvio.ESTANDAR);
        request.setItems(items);
        return request;
    }
}
