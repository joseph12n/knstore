package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.Carrito;
import com.mycompany.knstore.domain.Cuenta;
import com.mycompany.knstore.domain.ItemCarrito;
import com.mycompany.knstore.repository.CarritoRepository;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.ItemCarritoRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import com.mycompany.knstore.service.dto.CarritoDTO;
import com.mycompany.knstore.service.dto.CuentaDTO;
import com.mycompany.knstore.service.mapper.CarritoMapper;
import com.mycompany.knstore.service.mapper.CarritoMapperImpl;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarritoServiceImplTest {

    private static final Instant ANTES = Instant.now().minusSeconds(3600);

    private final CarritoMapper carritoMapper = new CarritoMapperImpl();

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private ItemCarritoRepository itemCarritoRepository;

    private CarritoServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CarritoServiceImpl(carritoRepository, cuentaRepository, itemCarritoRepository, carritoMapper);
    }

    private Carrito carritoConId(String id, String subtotal) {
        Carrito carrito = new Carrito();
        carrito.setId(id);
        carrito.setSubtotal(new BigDecimal(subtotal));
        return carrito;
    }

    private CarritoDTO carritoDTOConSubtotal(String subtotal) {
        CarritoDTO dto = new CarritoDTO();
        dto.setSubtotal(new BigDecimal(subtotal));
        CuentaDTO cuentaDTO = new CuentaDTO();
        cuentaDTO.setId("cuenta-1");
        dto.setCuenta(cuentaDTO);
        return dto;
    }

    @Test
    void savePersisteElCarritoYRetornaElDTOConId() {
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(invocation -> {
            Carrito carrito = invocation.getArgument(0);
            carrito.setId("car-1");
            return carrito;
        });

        CarritoDTO resultado = service.save(carritoDTOConSubtotal("25000"));

        assertThat(resultado.getId()).isEqualTo("car-1");
        assertThat(resultado.getSubtotal()).isEqualByComparingTo("25000");
        ArgumentCaptor<Carrito> captor = ArgumentCaptor.forClass(Carrito.class);
        verify(carritoRepository).save(captor.capture());
        assertThat(captor.getValue().getCuenta().getId()).isEqualTo("cuenta-1");
    }

    @Test
    void updatePersisteElCarritoYRetornaElDTOActualizado() {
        CarritoDTO dto = carritoDTOConSubtotal("30000");
        dto.setId("car-1");
        dto.setFechaActualizacion(ANTES);
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CarritoDTO resultado = service.update(dto);

        assertThat(resultado.getId()).isEqualTo("car-1");
        assertThat(resultado.getSubtotal()).isEqualByComparingTo("30000");
        assertThat(resultado.getFechaActualizacion()).isEqualTo(ANTES);
        verify(carritoRepository).save(any(Carrito.class));
    }

    @Test
    void partialUpdateAplicaSoloCamposNoNulos() {
        Carrito existente = carritoConId("car-1", "100");
        existente.setFechaActualizacion(ANTES);
        CarritoDTO dto = new CarritoDTO();
        dto.setId("car-1");
        dto.setSubtotal(new BigDecimal("200"));
        when(carritoRepository.findById("car-1")).thenReturn(Optional.of(existente));
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<CarritoDTO> resultado = service.partialUpdate(dto);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getSubtotal()).isEqualByComparingTo("200");
        ArgumentCaptor<Carrito> captor = ArgumentCaptor.forClass(Carrito.class);
        verify(carritoRepository).save(captor.capture());
        assertThat(captor.getValue().getFechaActualizacion()).isEqualTo(ANTES);
    }

    @Test
    void findAllComoClienteRetornaSoloLosCarritosDeSuCuenta() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(() -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)).thenReturn(true);
            security.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of("user-1"));
            Cuenta cuenta = new Cuenta();
            cuenta.setId("cuenta-1");
            when(cuentaRepository.findOneByUserId("user-1")).thenReturn(Optional.of(cuenta));
            Carrito carrito1 = carritoConId("car-1", "10000");
            carrito1.setCuenta(cuenta);
            Carrito carrito2 = carritoConId("car-2", "20000");
            carrito2.setCuenta(cuenta);
            when(carritoRepository.findByCuentaId("cuenta-1")).thenReturn(List.of(carrito1, carrito2));

            List<CarritoDTO> resultado = service.findAll();

            assertThat(resultado).extracting(CarritoDTO::getId).containsExactly("car-1", "car-2");
            assertThat(resultado.get(0).getCuenta().getId()).isEqualTo("cuenta-1");
            verify(carritoRepository, never()).findAll();
        }
    }

    @Test
    void findAllComoClienteSinCuentaAsociadaRetornaListaVacia() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(() -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)).thenReturn(true);
            security.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of("user-1"));
            when(cuentaRepository.findOneByUserId("user-1")).thenReturn(Optional.empty());

            List<CarritoDTO> resultado = service.findAll();

            assertThat(resultado).isEmpty();
            verify(carritoRepository, never()).findByCuentaId(any(String.class));
        }
    }

    @Test
    void findAllSinRolClienteDelegaAlRepositorioYRetornaTodos() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(() -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)).thenReturn(false);
            when(carritoRepository.findAll()).thenReturn(List.of(carritoConId("car-1", "10000"), carritoConId("car-2", "20000")));

            List<CarritoDTO> resultado = service.findAll();

            assertThat(resultado).extracting(CarritoDTO::getId).containsExactly("car-1", "car-2");
            verify(cuentaRepository, never()).findOneByUserId(any(String.class));
        }
    }

    @Test
    void findOneComoClienteRetornaElCarritoDeSuCuenta() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(() -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)).thenReturn(true);
            security.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of("user-1"));
            Cuenta cuenta = new Cuenta();
            cuenta.setId("cuenta-1");
            when(cuentaRepository.findOneByUserId("user-1")).thenReturn(Optional.of(cuenta));
            when(carritoRepository.findByIdAndCuentaId("car-1", "cuenta-1")).thenReturn(Optional.of(carritoConId("car-1", "10000")));

            Optional<CarritoDTO> resultado = service.findOne("car-1");

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getId()).isEqualTo("car-1");
        }
    }

    @Test
    void findOneComoClienteNoRetornaCarritosAjenos() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(() -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)).thenReturn(true);
            security.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of("user-1"));
            Cuenta cuenta = new Cuenta();
            cuenta.setId("cuenta-1");
            when(cuentaRepository.findOneByUserId("user-1")).thenReturn(Optional.of(cuenta));
            when(carritoRepository.findByIdAndCuentaId("car-9", "cuenta-1")).thenReturn(Optional.empty());

            Optional<CarritoDTO> resultado = service.findOne("car-9");

            assertThat(resultado).isEmpty();
            verify(carritoRepository, never()).findById(any(String.class));
        }
    }

    @Test
    void findOneSinRolClienteBuscaPorIdDirectamente() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(() -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)).thenReturn(false);
            when(carritoRepository.findById("car-1")).thenReturn(Optional.of(carritoConId("car-1", "10000")));

            Optional<CarritoDTO> resultado = service.findOne("car-1");

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getId()).isEqualTo("car-1");
            verify(carritoRepository, never()).findByIdAndCuentaId(any(String.class), any(String.class));
        }
    }

    @Test
    void deleteEliminaElCarritoPorId() {
        service.delete("car-1");

        verify(carritoRepository).deleteById("car-1");
    }

    @Test
    void vaciarEliminaLosItemsYReiniciaSubtotalYFecha() {
        ItemCarrito item1 = new ItemCarrito();
        item1.setId("item-1");
        ItemCarrito item2 = new ItemCarrito();
        item2.setId("item-2");
        when(itemCarritoRepository.findByCarritoId("car-1")).thenReturn(List.of(item1, item2));
        Carrito carrito = carritoConId("car-1", "50000");
        carrito.setFechaActualizacion(ANTES);
        when(carritoRepository.findById("car-1")).thenReturn(Optional.of(carrito));
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.vaciar("car-1");

        verify(itemCarritoRepository).deleteAll(List.of(item1, item2));
        ArgumentCaptor<Carrito> captor = ArgumentCaptor.forClass(Carrito.class);
        verify(carritoRepository).save(captor.capture());
        assertThat(captor.getValue().getSubtotal()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(captor.getValue().getFechaActualizacion()).isAfter(ANTES);
    }

    @Test
    void vaciarSinCarritoNoGuardaNada() {
        when(itemCarritoRepository.findByCarritoId("car-9")).thenReturn(List.of());
        when(carritoRepository.findById("car-9")).thenReturn(Optional.empty());

        service.vaciar("car-9");

        verify(itemCarritoRepository).deleteAll(List.of());
        verify(carritoRepository, never()).save(any());
    }
}
