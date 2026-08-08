package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.Carrito;
import com.mycompany.knstore.domain.ItemCarrito;
import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.domain.ProductoPrecio;
import com.mycompany.knstore.repository.CarritoRepository;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.ItemCarritoRepository;
import com.mycompany.knstore.repository.ProductoRepository;
import com.mycompany.knstore.service.dto.CarritoDTO;
import com.mycompany.knstore.service.dto.ItemCarritoDTO;
import com.mycompany.knstore.service.dto.ProductoDTO;
import com.mycompany.knstore.service.mapper.ItemCarritoMapper;
import com.mycompany.knstore.service.mapper.ItemCarritoMapperImpl;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ItemCarritoServiceImplTest {

    private static final BigDecimal PRECIO_VENTA = new BigDecimal("120000.00");

    private final ItemCarritoMapper itemCarritoMapper = new ItemCarritoMapperImpl();

    @Mock
    private ItemCarritoRepository itemCarritoRepository;

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private ProductoRepository productoRepository;

    private ItemCarritoServiceImpl service;

    private Carrito carrito;

    private Producto producto;

    @BeforeEach
    void setUp() {
        service = new ItemCarritoServiceImpl(
            itemCarritoRepository,
            carritoRepository,
            cuentaRepository,
            productoRepository,
            itemCarritoMapper
        );

        carrito = new Carrito();
        carrito.setId("carrito-1");
        carrito.setSubtotal(BigDecimal.ZERO);
        carrito.setFechaActualizacion(Instant.now());

        ProductoPrecio precio = new ProductoPrecio();
        precio.setId("precio-1");
        precio.setPrecioVenta(PRECIO_VENTA);

        producto = new Producto();
        producto.setId("producto-1");
        producto.setPrecio(precio);
    }

    private static CarritoDTO carritoDto(String id) {
        CarritoDTO dto = new CarritoDTO();
        dto.setId(id);
        return dto;
    }

    private static ProductoDTO productoDto(String id) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(id);
        return dto;
    }

    @Test
    void crearItemRecalculaSubtotalDelCarritoYUsaPrecioDelProducto() {
        ItemCarritoDTO dto = new ItemCarritoDTO();
        dto.setCantidad(2);
        dto.setCarrito(carritoDto("carrito-1"));
        dto.setProducto(productoDto("producto-1"));

        ItemCarrito[] guardado = new ItemCarrito[1];
        when(itemCarritoRepository.save(any())).thenAnswer(invocation -> {
            ItemCarrito item = invocation.getArgument(0);
            if (item.getId() == null) {
                item.setId("item-1");
            }
            guardado[0] = item;
            return item;
        });
        when(productoRepository.findById("producto-1")).thenReturn(Optional.of(producto));
        when(carritoRepository.findById("carrito-1")).thenReturn(Optional.of(carrito));
        when(itemCarritoRepository.findByCarritoId("carrito-1")).thenAnswer(invocation -> List.of(guardado[0]));
        when(carritoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ItemCarritoDTO result = service.save(dto);

        assertThat(result.getSubtotal()).isEqualByComparingTo(new BigDecimal("240000"));
        assertThat(result.getPrecioUnitario()).isEqualByComparingTo(PRECIO_VENTA);
        ArgumentCaptor<Carrito> captor = ArgumentCaptor.forClass(Carrito.class);
        verify(carritoRepository).save(captor.capture());
        assertThat(captor.getValue().getSubtotal()).isEqualByComparingTo(new BigDecimal("240000"));
        assertThat(captor.getValue().getFechaActualizacion()).isNotNull();
    }

    @Test
    void editarItemRecalculaSubtotalDelCarrito() {
        ItemCarrito[] guardado = new ItemCarrito[1];
        when(itemCarritoRepository.save(any())).thenAnswer(invocation -> {
            guardado[0] = invocation.getArgument(0);
            return guardado[0];
        });
        when(itemCarritoRepository.findByCarritoId("carrito-1")).thenAnswer(invocation -> List.of(guardado[0]));
        when(carritoRepository.findById("carrito-1")).thenReturn(Optional.of(carrito));
        when(carritoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ItemCarritoDTO dto = new ItemCarritoDTO();
        dto.setId("item-1");
        dto.setCantidad(3);
        dto.setPrecioUnitario(new BigDecimal("100000"));
        dto.setCarrito(carritoDto("carrito-1"));
        dto.setProducto(productoDto("producto-1"));

        ItemCarritoDTO result = service.update(dto);

        assertThat(result.getSubtotal()).isEqualByComparingTo(new BigDecimal("300000"));
        ArgumentCaptor<Carrito> captor = ArgumentCaptor.forClass(Carrito.class);
        verify(carritoRepository).save(captor.capture());
        assertThat(captor.getValue().getSubtotal()).isEqualByComparingTo(new BigDecimal("300000"));
    }

    @Test
    void eliminarItemRecalculaSubtotalDelCarrito() {
        ItemCarrito aEliminar = new ItemCarrito();
        aEliminar.setId("item-1");
        aEliminar.setCarrito(carrito);
        aEliminar.setSubtotal(new BigDecimal("120000"));

        ItemCarrito restante = new ItemCarrito();
        restante.setId("item-2");
        restante.setCarrito(carrito);
        restante.setSubtotal(new BigDecimal("80000"));

        when(itemCarritoRepository.findById("item-1")).thenReturn(Optional.of(aEliminar));
        when(itemCarritoRepository.findByCarritoId("carrito-1")).thenReturn(List.of(restante));
        when(carritoRepository.findById("carrito-1")).thenReturn(Optional.of(carrito));
        when(carritoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.delete("item-1");

        ArgumentCaptor<Carrito> captor = ArgumentCaptor.forClass(Carrito.class);
        verify(carritoRepository).save(captor.capture());
        assertThat(captor.getValue().getSubtotal()).isEqualByComparingTo(new BigDecimal("80000"));
    }
}
