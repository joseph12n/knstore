package com.mycompany.knstore.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.Carrito;
import com.mycompany.knstore.domain.Cuenta;
import com.mycompany.knstore.domain.Factura;
import com.mycompany.knstore.domain.Pago;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.repository.CarritoRepository;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.DireccionRepository;
import com.mycompany.knstore.repository.EnvioRepository;
import com.mycompany.knstore.repository.FacturaRepository;
import com.mycompany.knstore.repository.ItemCarritoRepository;
import com.mycompany.knstore.repository.ItemPedidoRepository;
import com.mycompany.knstore.repository.PagoRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.service.ResourceAccessService;
import com.mycompany.knstore.service.dto.CarritoDTO;
import com.mycompany.knstore.service.dto.DireccionDTO;
import com.mycompany.knstore.service.dto.FacturaDTO;
import com.mycompany.knstore.service.dto.ItemCarritoDTO;
import com.mycompany.knstore.service.dto.PagoDTO;
import com.mycompany.knstore.service.dto.UserDTO;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class ResourceAccessServiceTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private DireccionRepository direccionRepository;

    @Mock
    private ItemCarritoRepository itemCarritoRepository;

    @Mock
    private ItemPedidoRepository itemPedidoRepository;

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private EnvioRepository envioRepository;

    @Mock
    private FacturaRepository facturaRepository;

    private ResourceAccessService resourceAccessService;

    @BeforeEach
    void setUp() {
        resourceAccessService = new ResourceAccessService(
            cuentaRepository,
            carritoRepository,
            pedidoRepository,
            direccionRepository,
            itemCarritoRepository,
            itemPedidoRepository,
            pagoRepository,
            envioRepository,
            facturaRepository
        );
    }

    @AfterEach
    void clearSecurity() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void adminCanAccessAnyCuentaId() {
        authenticate("admin", AuthoritiesConstants.ADMIN);

        boolean canAccess = resourceAccessService.canAccessCuentaId("cuenta-1");

        assertThat(canAccess).isTrue();
    }

    @Test
    void clienteCanAccessOwnCuentaId() {
        authenticate("cliente", AuthoritiesConstants.CLIENTE);
        when(cuentaRepository.findByIdAndUserId("cuenta-1", "cliente")).thenReturn(Optional.of(new Cuenta()));

        boolean canAccess = resourceAccessService.canAccessCuentaId("cuenta-1");

        assertThat(canAccess).isTrue();
    }

    @Test
    void clienteCannotAccessAnotherCuentaId() {
        authenticate("cliente", AuthoritiesConstants.CLIENTE);
        when(cuentaRepository.findByIdAndUserId("cuenta-1", "cliente")).thenReturn(Optional.empty());

        boolean canAccess = resourceAccessService.canAccessCuentaId("cuenta-1");

        assertThat(canAccess).isFalse();
    }

    @Test
    void clienteCanAccessItemCarritoWhenCarritoIsOwn() {
        authenticate("cliente", AuthoritiesConstants.CLIENTE);
        Cuenta cuenta = new Cuenta();
        cuenta.setId("cuenta-1");
        when(cuentaRepository.findOneByUserId("cliente")).thenReturn(Optional.of(cuenta));
        when(carritoRepository.findByIdAndCuentaId("carrito-1", "cuenta-1")).thenReturn(Optional.of(new Carrito()));

        ItemCarritoDTO itemCarritoDTO = new ItemCarritoDTO();
        CarritoDTO carritoDTO = new CarritoDTO();
        carritoDTO.setId("carrito-1");
        itemCarritoDTO.setCarrito(carritoDTO);

        boolean canAccess = resourceAccessService.canAccessItemCarritoDto(itemCarritoDTO);

        assertThat(canAccess).isTrue();
    }

    @Test
    void clienteCanAccessFacturaWhenPagoBelongsToUser() {
        authenticate("cliente", AuthoritiesConstants.CLIENTE);

        Cuenta cuenta = new Cuenta();
        cuenta.setId("cuenta-1");
        when(cuentaRepository.findOneByUserId("cliente")).thenReturn(Optional.of(cuenta));

        Pedido pedido = new Pedido();
        pedido.setId("pedido-1");
        when(pedidoRepository.findByCuentaId("cuenta-1", org.springframework.data.domain.Pageable.unpaged())).thenReturn(
            new org.springframework.data.domain.PageImpl<>(java.util.List.of(pedido))
        );

        Pago pago = new Pago();
        pago.setId("pago-1");
        when(pagoRepository.findByIdAndPedidoId("pago-1", "pedido-1")).thenReturn(Optional.of(pago));

        FacturaDTO facturaDTO = new FacturaDTO();
        PagoDTO pagoDTO = new PagoDTO();
        pagoDTO.setId("pago-1");
        facturaDTO.setPago(pagoDTO);

        boolean canAccess = resourceAccessService.canAccessFacturaDto(facturaDTO);

        assertThat(canAccess).isTrue();
    }

    @Test
    void userRoleWithoutClienteCannotAccessProtectedOwnership() {
        authenticate("user", AuthoritiesConstants.USER);

        boolean canAccess = resourceAccessService.canAccessCuentaId("cuenta-1");

        assertThat(canAccess).isFalse();
    }

    @Test
    void clienteCanAccessCuentaDtoMatchingLogin() {
        authenticate("cliente", AuthoritiesConstants.CLIENTE);

        com.mycompany.knstore.service.dto.CuentaDTO cuentaDTO = new com.mycompany.knstore.service.dto.CuentaDTO();
        UserDTO userDTO = new UserDTO();
        userDTO.setLogin("cliente");
        cuentaDTO.setUser(userDTO);

        boolean canAccess = resourceAccessService.canAccessCuentaDto(cuentaDTO);

        assertThat(canAccess).isTrue();
    }

    @Test
    void clienteCanAccessOwnDireccionDto() {
        authenticate("cliente", AuthoritiesConstants.CLIENTE);
        when(cuentaRepository.findByIdAndUserId("cuenta-1", "cliente")).thenReturn(Optional.of(new Cuenta()));

        com.mycompany.knstore.service.dto.CuentaDTO cuentaDTO = new com.mycompany.knstore.service.dto.CuentaDTO();
        cuentaDTO.setId("cuenta-1");
        DireccionDTO direccionDTO = new DireccionDTO();
        direccionDTO.setCuenta(cuentaDTO);

        boolean canAccess = resourceAccessService.canAccessDireccionDto(direccionDTO);

        assertThat(canAccess).isTrue();
    }

    @Test
    void clienteCannotAccessAnotherDireccionDto() {
        authenticate("cliente", AuthoritiesConstants.CLIENTE);
        when(cuentaRepository.findByIdAndUserId("cuenta-2", "cliente")).thenReturn(Optional.empty());

        com.mycompany.knstore.service.dto.CuentaDTO cuentaDTO = new com.mycompany.knstore.service.dto.CuentaDTO();
        cuentaDTO.setId("cuenta-2");
        DireccionDTO direccionDTO = new DireccionDTO();
        direccionDTO.setCuenta(cuentaDTO);

        boolean canAccess = resourceAccessService.canAccessDireccionDto(direccionDTO);

        assertThat(canAccess).isFalse();
    }

    @Test
    void adminCanAccessFacturaDtoWithoutOwnershipLookup() {
        authenticate("admin", AuthoritiesConstants.ADMIN);

        FacturaDTO facturaDTO = new FacturaDTO();
        PagoDTO pagoDTO = new PagoDTO();
        pagoDTO.setId("pago-1");
        facturaDTO.setPago(pagoDTO);

        boolean canAccess = resourceAccessService.canAccessFacturaDto(facturaDTO);

        assertThat(canAccess).isTrue();
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
        List<SimpleGrantedAuthority> grantedAuthorities = java.util.Arrays.stream(authorities).map(SimpleGrantedAuthority::new).toList();
        JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
