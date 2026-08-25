package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.Factura;
import com.mycompany.knstore.domain.Pago;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.FacturaRepository;
import com.mycompany.knstore.repository.PagoRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import com.mycompany.knstore.service.dto.FacturaDTO;
import com.mycompany.knstore.service.mapper.FacturaMapper;
import com.mycompany.knstore.service.mapper.FacturaMapperImpl;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class FacturaServiceImplTest {

    private final FacturaMapper facturaMapper = new FacturaMapperImpl();

    @Mock
    private FacturaRepository facturaRepository;

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    private FacturaServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new FacturaServiceImpl(facturaRepository, pagoRepository, pedidoRepository, cuentaRepository, facturaMapper);
    }

    @AfterEach
    void clearSecurity() {
        SecurityContextHolder.clearContext();
    }

    private void authenticate(String login, String... authorities) {
        Jwt jwt = Jwt.withTokenValue("dummy-token")
            .header("alg", "HS512")
            .subject(login)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .claim(SecurityUtils.AUTHORITIES_CLAIM, String.join(" ", authorities))
            .claim(SecurityUtils.USER_ID_CLAIM, login)
            .build();
        List<SimpleGrantedAuthority> grantedAuthorities = Arrays.stream(authorities).map(SimpleGrantedAuthority::new).toList();
        JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void findAllClienteUsaConsultasEnLotePorPedidosYPagosDeLaCuenta() {
        authenticate("cliente", AuthoritiesConstants.CLIENTE);
        com.mycompany.knstore.domain.Cuenta cuenta = new com.mycompany.knstore.domain.Cuenta();
        cuenta.setId("cuenta-1");
        when(cuentaRepository.findOneByUserId("cliente")).thenReturn(Optional.of(cuenta));

        Pedido pedido = new Pedido();
        pedido.setId("p-1");
        when(pedidoRepository.findByCuentaId("cuenta-1", Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(pedido)));

        Pago pago = new Pago();
        pago.setId("pg-1");
        pago.setPedido(pedido);
        when(pagoRepository.findByPedidoIdIn(any())).thenReturn(List.of(pago));

        Factura factura = new Factura();
        factura.setId("f-1");
        factura.setPago(pago);
        when(facturaRepository.findByPagoIdIn(any(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(factura)));

        Page<FacturaDTO> result = service.findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id")));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent())
            .singleElement()
            .satisfies(dto -> assertThat(dto.getId()).isEqualTo("f-1"));
        verify(pedidoRepository).findByCuentaId(eq("cuenta-1"), eq(Pageable.unpaged()));
        verify(pagoRepository).findByPedidoIdIn(any());
        verify(facturaRepository).findByPagoIdIn(any(), any(Pageable.class));
        verify(facturaRepository, never()).findByPagoId(any(), any(Pageable.class));
    }

    @Test
    void findOneClienteVerificaOwnershipPagoYPedidoSinRecorrerListas() {
        authenticate("cliente", AuthoritiesConstants.CLIENTE);
        com.mycompany.knstore.domain.Cuenta cuenta = new com.mycompany.knstore.domain.Cuenta();
        cuenta.setId("cuenta-1");
        when(cuentaRepository.findOneByUserId("cliente")).thenReturn(Optional.of(cuenta));

        Pedido pedido = new Pedido();
        pedido.setId("p-1");
        when(pedidoRepository.findByIdAndCuentaId("p-1", "cuenta-1")).thenReturn(Optional.of(pedido));

        Pago pago = new Pago();
        pago.setId("pg-1");
        pago.setPedido(pedido);
        when(pagoRepository.findById("pg-1")).thenReturn(Optional.of(pago));

        Factura factura = new Factura();
        factura.setId("f-1");
        factura.setPago(pago);
        when(facturaRepository.findById("f-1")).thenReturn(Optional.of(factura));

        Optional<FacturaDTO> result = service.findOne("f-1");

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().getId()).isEqualTo("f-1");
        verify(facturaRepository).findById("f-1");
        verify(pagoRepository).findById("pg-1");
        verify(pedidoRepository).findByIdAndCuentaId(eq("p-1"), eq("cuenta-1"));
        verify(pedidoRepository, never()).findByCuentaId(any(), any());
        verify(facturaRepository, never()).findByPagoId(any(), any(Pageable.class));
    }

    @Test
    void findOneClienteConPedidoDeOtraCuentaDevuelveVacio() {
        authenticate("cliente", AuthoritiesConstants.CLIENTE);
        com.mycompany.knstore.domain.Cuenta cuenta = new com.mycompany.knstore.domain.Cuenta();
        cuenta.setId("cuenta-1");
        when(cuentaRepository.findOneByUserId("cliente")).thenReturn(Optional.of(cuenta));

        Pedido pedido = new Pedido();
        pedido.setId("p-1");
        when(pedidoRepository.findByIdAndCuentaId("p-1", "cuenta-1")).thenReturn(Optional.empty());

        Pago pago = new Pago();
        pago.setId("pg-1");
        pago.setPedido(pedido);
        when(pagoRepository.findById("pg-1")).thenReturn(Optional.of(pago));

        Factura factura = new Factura();
        factura.setId("f-1");
        factura.setPago(pago);
        when(facturaRepository.findById("f-1")).thenReturn(Optional.of(factura));

        Optional<FacturaDTO> result = service.findOne("f-1");

        assertThat(result).isEmpty();
    }
}
