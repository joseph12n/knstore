package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.Envio;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.domain.enumeration.EstadoEnvio;
import com.mycompany.knstore.domain.enumeration.EstadoPedido;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.EnvioRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import com.mycompany.knstore.service.HistorialEstadoService;
import com.mycompany.knstore.service.dto.EnvioDTO;
import com.mycompany.knstore.service.mapper.EnvioMapper;
import com.mycompany.knstore.service.mapper.EnvioMapperImpl;
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
class EnvioServiceImplTest {

    private final EnvioMapper envioMapper = new EnvioMapperImpl();

    @Mock
    private EnvioRepository envioRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private HistorialEstadoService historialEstadoService;

    private EnvioServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new EnvioServiceImpl(envioRepository, pedidoRepository, cuentaRepository, envioMapper, historialEstadoService);
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

    private Envio envioConEstado(EstadoEnvio estado) {
        Envio envio = new Envio();
        envio.setId("e-1");
        envio.setEstado(estado);
        return envio;
    }

    @Test
    void asignarTrackingGuardaTransportadoraYNumero() {
        Envio envio = envioConEstado(EstadoEnvio.PENDING);
        when(envioRepository.findById("e-1")).thenReturn(Optional.of(envio));
        when(envioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        EnvioDTO result = service.asignarTracking("e-1", "Interrapidismo", "TRK-123");

        assertThat(result.getTransportadora()).isEqualTo("Interrapidismo");
        assertThat(result.getNumeroRastreo()).isEqualTo("TRK-123");
        verify(historialEstadoService).registrar(eq("ENVIO"), eq("e-1"), eq("tracking"), any(), any());
    }

    @Test
    void cambiarEstadoAvanceValidoRegistraEnHistorial() {
        Envio envio = envioConEstado(EstadoEnvio.PENDING);
        when(envioRepository.findById("e-1")).thenReturn(Optional.of(envio));
        when(envioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        EnvioDTO result = service.cambiarEstado("e-1", EstadoEnvio.IN_TRANSIT);

        assertThat(result.getEstado()).isEqualTo(EstadoEnvio.IN_TRANSIT);
        verify(historialEstadoService).registrar(eq("ENVIO"), eq("e-1"), eq("estado"), eq("PENDING"), eq("IN_TRANSIT"));
    }

    @Test
    void cambiarEstadoInvalidoLanzaExcepcion() {
        Envio envio = envioConEstado(EstadoEnvio.DELIVERED);
        when(envioRepository.findById("e-1")).thenReturn(Optional.of(envio));

        assertThatThrownBy(() -> service.cambiarEstado("e-1", EstadoEnvio.PENDING))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Transicion invalida");
    }

    @Test
    void marcarDevolucionMarcaEnvioYPedidoDevueltos() {
        Envio envio = envioConEstado(EstadoEnvio.IN_TRANSIT);
        Pedido pedido = new Pedido();
        pedido.setId("p-1");
        pedido.setEstado(EstadoPedido.SHIPPED);
        envio.setPedido(pedido);

        when(envioRepository.findById("e-1")).thenReturn(Optional.of(envio));
        when(envioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(pedidoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        EnvioDTO result = service.marcarDevolucion("e-1");

        assertThat(result.getEstado()).isEqualTo(EstadoEnvio.RETURNED);
        verify(historialEstadoService).registrar(eq("PEDIDO"), eq("p-1"), eq("estado"), eq("SHIPPED"), eq("RETURNED"));
    }

    @Test
    void findAllClienteUsaConsultaEnLotePorPedidosDeLaCuenta() {
        authenticate("cliente", AuthoritiesConstants.CLIENTE);
        com.mycompany.knstore.domain.Cuenta cuenta = new com.mycompany.knstore.domain.Cuenta();
        cuenta.setId("cuenta-1");
        when(cuentaRepository.findOneByUserId("cliente")).thenReturn(Optional.of(cuenta));

        Pedido pedido = new Pedido();
        pedido.setId("p-1");
        when(pedidoRepository.findByCuentaId("cuenta-1", Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(pedido)));

        Envio envio = envioConEstado(EstadoEnvio.PENDING);
        envio.setPedido(pedido);
        when(envioRepository.findByPedidoIdIn(any(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(envio)));

        Page<EnvioDTO> result = service.findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id")));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent())
            .singleElement()
            .satisfies(dto -> assertThat(dto.getId()).isEqualTo("e-1"));
        verify(pedidoRepository).findByCuentaId(eq("cuenta-1"), eq(Pageable.unpaged()));
        verify(envioRepository).findByPedidoIdIn(any(), any(Pageable.class));
        verify(envioRepository, never()).findByPedidoId(any(), any(Pageable.class));
    }

    @Test
    void findOneClienteVerificaOwnershipSinRecorrerListas() {
        authenticate("cliente", AuthoritiesConstants.CLIENTE);
        com.mycompany.knstore.domain.Cuenta cuenta = new com.mycompany.knstore.domain.Cuenta();
        cuenta.setId("cuenta-1");
        when(cuentaRepository.findOneByUserId("cliente")).thenReturn(Optional.of(cuenta));

        Pedido pedido = new Pedido();
        pedido.setId("p-1");
        when(pedidoRepository.findByIdAndCuentaId("p-1", "cuenta-1")).thenReturn(Optional.of(pedido));

        Envio envio = envioConEstado(EstadoEnvio.PENDING);
        envio.setPedido(pedido);
        when(envioRepository.findById("e-1")).thenReturn(Optional.of(envio));

        Optional<EnvioDTO> result = service.findOne("e-1");

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().getId()).isEqualTo("e-1");
        verify(envioRepository).findById("e-1");
        verify(pedidoRepository).findByIdAndCuentaId(eq("p-1"), eq("cuenta-1"));
        verify(pedidoRepository, never()).findByCuentaId(any(), any());
        verify(envioRepository, never()).findByPedidoId(any(), any(Pageable.class));
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

        Envio envio = envioConEstado(EstadoEnvio.PENDING);
        envio.setPedido(pedido);
        when(envioRepository.findById("e-1")).thenReturn(Optional.of(envio));

        Optional<EnvioDTO> result = service.findOne("e-1");

        assertThat(result).isEmpty();
    }
}
