package com.mycompany.knstore.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.Carrito;
import com.mycompany.knstore.domain.Cuenta;
import com.mycompany.knstore.domain.Factura;
import com.mycompany.knstore.repository.CarritoRepository;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.DireccionRepository;
import com.mycompany.knstore.repository.EnvioRepository;
import com.mycompany.knstore.repository.FacturaRepository;
import com.mycompany.knstore.repository.ItemCarritoRepository;
import com.mycompany.knstore.repository.ItemPedidoRepository;
import com.mycompany.knstore.repository.PagoRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.service.dto.CarritoDTO;
import com.mycompany.knstore.service.dto.FacturaDTO;
import com.mycompany.knstore.service.dto.ItemCarritoDTO;
import com.mycompany.knstore.service.dto.PagoDTO;
import com.mycompany.knstore.service.dto.UserDTO;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

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
        when(carritoRepository.findByIdAndCuentaId("carrito-1", "cliente")).thenReturn(Optional.of(new Carrito()));

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
        when(facturaRepository.findByIdAndPagoId("factura-1", "cliente")).thenReturn(Optional.of(new Factura()));

        boolean canAccess = resourceAccessService.canAccessFacturaId("factura-1");

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
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            login,
            "n/a",
            AuthorityUtils.createAuthorityList(authorities)
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
