package com.mycompany.knstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.CategoriaIVA;
import com.mycompany.knstore.domain.Cuenta;
import com.mycompany.knstore.domain.Direccion;
import com.mycompany.knstore.domain.Envio;
import com.mycompany.knstore.domain.ItemPedido;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.domain.ProductoInventario;
import com.mycompany.knstore.domain.ProductoPrecio;
import com.mycompany.knstore.domain.enumeration.EstadoEnvio;
import com.mycompany.knstore.domain.enumeration.EstadoPedido;
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
import com.mycompany.knstore.service.dto.CheckoutResultDTO;
import com.mycompany.knstore.service.dto.PedidoDTO;
import com.mycompany.knstore.service.mapper.ItemPedidoMapper;
import com.mycompany.knstore.service.mapper.PedidoMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceS03UnitTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ItemPedidoRepository itemPedidoRepository;

    @Mock
    private EnvioRepository envioRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ProductoInventarioRepository productoInventarioRepository;

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private ItemCarritoRepository itemCarritoRepository;

    @Mock
    private DireccionRepository direccionRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private HistorialEstadoService historialEstadoService;

    @Mock
    private PedidoMapper pedidoMapper;

    @Mock
    private ItemPedidoMapper itemPedidoMapper;

    @InjectMocks
    private CheckoutService checkoutService;

    @Test
    void checkoutConConcurrenciaEnStockLanzaExcepcion() {
        Cuenta cuenta = buildCuenta();
        CheckoutRequestDTO request = buildRequest();
        Direccion direccion = buildDireccion(cuenta);
        Producto producto = buildProductoConStockYPrecio("prod-1", 5, new BigDecimal("10000"));

        when(direccionRepository.findById("dir-1")).thenReturn(Optional.of(direccion));
        when(productoRepository.findById("prod-1")).thenReturn(Optional.of(producto));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            if (p.getId() == null) {
                p.setId("ped-1");
            }
            return p;
        });
        when(itemPedidoRepository.save(any(ItemPedido.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mongoTemplate.findAndModify(any(), any(), any(), eq(Document.class), eq("pedido_sequence"))).thenReturn(
            new Document("seq", 1L)
        );
        when(mongoTemplate.findAndModify(any(), any(), any(), eq(ProductoInventario.class))).thenReturn(null);

        CheckoutException ex = assertThrows(CheckoutException.class, () -> checkoutService.checkout(cuenta, request));

        assertThat(ex.getMessage()).contains("posible concurrencia");
    }

    @Test
    void checkoutExitosoCreaPedidoYEnvioPendientes() {
        Cuenta cuenta = buildCuenta();
        CheckoutRequestDTO request = buildRequest();
        Direccion direccion = buildDireccion(cuenta);
        Producto producto = buildProductoConStockYPrecio("prod-1", 5, new BigDecimal("10000"));

        when(direccionRepository.findById("dir-1")).thenReturn(Optional.of(direccion));
        when(productoRepository.findById("prod-1")).thenReturn(Optional.of(producto));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            if (p.getId() == null) {
                p.setId("ped-1");
            }
            return p;
        });
        when(itemPedidoRepository.save(any(ItemPedido.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mongoTemplate.findAndModify(any(), any(), any(), eq(Document.class), eq("pedido_sequence"))).thenReturn(
            new Document("seq", 1L)
        );
        when(mongoTemplate.findAndModify(any(), any(), any(), eq(ProductoInventario.class))).thenReturn(new ProductoInventario());
        when(envioRepository.save(any(Envio.class))).thenAnswer(invocation -> {
            Envio envio = invocation.getArgument(0);
            envio.setId("env-1");
            return envio;
        });
        when(carritoRepository.findByCuentaId("cuenta-1")).thenReturn(List.of());

        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setId("ped-1");
        when(pedidoMapper.toDto(any(Pedido.class))).thenReturn(pedidoDTO);

        CheckoutResultDTO result = checkoutService.checkout(cuenta, request);

        assertThat(result).isNotNull();
        assertThat(result.getPedido()).isNotNull();
        assertThat(result.getPedido().getId()).isEqualTo("ped-1");

        verify(historialEstadoService).registrarCambioEstado("Pedido", "ped-1", "estado", null, EstadoPedido.PENDING.name());

        ArgumentCaptor<Envio> envioCaptor = ArgumentCaptor.forClass(Envio.class);
        verify(envioRepository).save(envioCaptor.capture());
        assertThat(envioCaptor.getValue().getEstado()).isEqualTo(EstadoEnvio.PENDING);
    }

    private Cuenta buildCuenta() {
        Cuenta cuenta = new Cuenta();
        cuenta.setId("cuenta-1");
        return cuenta;
    }

    private Direccion buildDireccion(Cuenta cuenta) {
        Direccion direccion = new Direccion();
        direccion.setId("dir-1");
        direccion.setCuenta(cuenta);
        return direccion;
    }

    private CheckoutRequestDTO buildRequest() {
        CheckoutItemDTO item = new CheckoutItemDTO();
        item.setProductoId("prod-1");
        item.setCantidad(1);
        item.setPrecioUnitario(new BigDecimal("10000"));

        CheckoutRequestDTO request = new CheckoutRequestDTO();
        request.setDireccionId("dir-1");
        request.setMetodoPago(MetodoPago.NEQUI);
        request.setTipoServicioEnvio(TipoServicioEnvio.ESTANDAR);
        request.setItems(List.of(item));
        return request;
    }

    private Producto buildProductoConStockYPrecio(String productoId, int stock, BigDecimal precioVenta) {
        ProductoInventario inventario = new ProductoInventario();
        inventario.setId("inv-1");
        inventario.setStock(stock);

        ProductoPrecio precio = new ProductoPrecio();
        precio.setPrecioVenta(precioVenta);

        CategoriaIVA categoriaIVA = new CategoriaIVA();
        categoriaIVA.setPorcentaje(BigDecimal.valueOf(19));

        Producto producto = new Producto();
        producto.setId(productoId);
        producto.setNombre("Producto test");
        producto.setSlug("producto-test");
        producto.setInventario(inventario);
        producto.setPrecio(precio);
        producto.setCategoriaIva(categoriaIVA);
        return producto;
    }
}
