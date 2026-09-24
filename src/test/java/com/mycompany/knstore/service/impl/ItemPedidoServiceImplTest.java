package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.Cuenta;
import com.mycompany.knstore.domain.ItemPedido;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.ItemPedidoRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import com.mycompany.knstore.service.dto.ItemPedidoDTO;
import com.mycompany.knstore.service.dto.PedidoDTO;
import com.mycompany.knstore.service.dto.ProductoDTO;
import com.mycompany.knstore.service.mapper.ItemPedidoMapper;
import com.mycompany.knstore.service.mapper.ItemPedidoMapperImpl;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class ItemPedidoServiceImplTest {

    private final ItemPedidoMapper itemPedidoMapper = new ItemPedidoMapperImpl();

    @Mock
    private ItemPedidoRepository itemPedidoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    private ItemPedidoServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ItemPedidoServiceImpl(itemPedidoRepository, pedidoRepository, cuentaRepository, itemPedidoMapper);
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

    private static PedidoDTO pedidoDto(String id) {
        PedidoDTO dto = new PedidoDTO();
        dto.setId(id);
        return dto;
    }

    private static ProductoDTO productoDto(String id) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(id);
        return dto;
    }

    private static ItemPedido itemPedido(String id, String pedidoId, int cantidad, String precio) {
        ItemPedido item = new ItemPedido();
        item.setId(id);
        Pedido pedido = new Pedido();
        pedido.setId(pedidoId);
        item.setPedido(pedido);
        item.setCantidad(cantidad);
        item.setPrecioUnitario(new BigDecimal(precio));
        return item;
    }

    @Test
    void savePersisteEntidadYRetornaDto() {
        ItemPedidoDTO dto = new ItemPedidoDTO();
        dto.setNombreProducto("Tenis Nike Air");
        dto.setCantidad(2);
        dto.setPrecioUnitario(new BigDecimal("120000.00"));
        dto.setPorcentajeIva(new BigDecimal("19"));
        dto.setValorIva(new BigDecimal("22800.00"));
        dto.setSubtotal(new BigDecimal("240000.00"));
        dto.setPedido(pedidoDto("pedido-1"));
        dto.setProducto(productoDto("producto-1"));
        when(itemPedidoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ItemPedidoDTO result = service.save(dto);

        ArgumentCaptor<ItemPedido> captor = ArgumentCaptor.forClass(ItemPedido.class);
        verify(itemPedidoRepository).save(captor.capture());
        assertThat(captor.getValue().getNombreProducto()).isEqualTo("Tenis Nike Air");
        assertThat(captor.getValue().getCantidad()).isEqualTo(2);
        assertThat(captor.getValue().getPrecioUnitario()).isEqualByComparingTo(new BigDecimal("120000.00"));
        assertThat(captor.getValue().getPedido()).isNotNull();
        assertThat(captor.getValue().getPedido().getId()).isEqualTo("pedido-1");
        assertThat(captor.getValue().getProducto()).isNotNull();
        assertThat(captor.getValue().getProducto().getId()).isEqualTo("producto-1");
        assertThat(result.getNombreProducto()).isEqualTo("Tenis Nike Air");
        assertThat(result.getCantidad()).isEqualTo(2);
        assertThat(result.getSubtotal()).isEqualByComparingTo(new BigDecimal("240000.00"));
    }

    @Test
    void updateGuardaEntidadConIdYRetornaDto() {
        ItemPedidoDTO dto = new ItemPedidoDTO();
        dto.setId("item-1");
        dto.setNombreProducto("Tenis Adidas Superstar");
        dto.setCantidad(3);
        dto.setPrecioUnitario(new BigDecimal("95000.00"));
        dto.setPedido(pedidoDto("pedido-1"));
        dto.setProducto(productoDto("producto-1"));
        when(itemPedidoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ItemPedidoDTO result = service.update(dto);

        ArgumentCaptor<ItemPedido> captor = ArgumentCaptor.forClass(ItemPedido.class);
        verify(itemPedidoRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo("item-1");
        assertThat(captor.getValue().getCantidad()).isEqualTo(3);
        assertThat(captor.getValue().getPrecioUnitario()).isEqualByComparingTo(new BigDecimal("95000.00"));
        assertThat(result.getId()).isEqualTo("item-1");
        assertThat(result.getCantidad()).isEqualTo(3);
    }

    @Test
    void partialUpdateAplicaSoloCamposNoNulos() {
        ItemPedido existente = new ItemPedido();
        existente.setId("item-1");
        existente.setNombreProducto("Tenis Nike Air");
        existente.setCantidad(2);
        existente.setPrecioUnitario(new BigDecimal("120000.00"));
        existente.setSubtotal(new BigDecimal("240000.00"));
        when(itemPedidoRepository.findById("item-1")).thenReturn(Optional.of(existente));
        when(itemPedidoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ItemPedidoDTO dto = new ItemPedidoDTO();
        dto.setId("item-1");
        dto.setCantidad(5);

        Optional<ItemPedidoDTO> result = service.partialUpdate(dto);

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().getId()).isEqualTo("item-1");
        ArgumentCaptor<ItemPedido> captor = ArgumentCaptor.forClass(ItemPedido.class);
        verify(itemPedidoRepository).save(captor.capture());
        assertThat(captor.getValue().getCantidad()).isEqualTo(5);
        assertThat(captor.getValue().getNombreProducto()).isEqualTo("Tenis Nike Air");
        assertThat(captor.getValue().getPrecioUnitario()).isEqualByComparingTo(new BigDecimal("120000.00"));
        assertThat(captor.getValue().getSubtotal()).isEqualByComparingTo(new BigDecimal("240000.00"));
    }

    @Test
    void partialUpdateRetornaVacioCuandoNoExiste() {
        ItemPedidoDTO dto = new ItemPedidoDTO();
        dto.setId("no-existe");
        when(itemPedidoRepository.findById("no-existe")).thenReturn(Optional.empty());

        Optional<ItemPedidoDTO> result = service.partialUpdate(dto);

        assertThat(result).isEmpty();
        verify(itemPedidoRepository, org.mockito.Mockito.never()).save(any());
    }

    @Test
    void findAllAdminRetornaTodosLosItems() {
        ItemPedido primero = itemPedido("item-1", "pedido-1", 2, "120000");
        ItemPedido segundo = itemPedido("item-2", "pedido-2", 1, "95000");
        when(itemPedidoRepository.findAll()).thenReturn(List.of(primero, segundo));

        List<ItemPedidoDTO> result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ItemPedidoDTO::getId).containsExactly("item-1", "item-2");
        verify(itemPedidoRepository, org.mockito.Mockito.never()).findByPedidoId(any());
    }

    @Test
    void findAllClienteRetornaSoloItemsDeSusPedidos() {
        authenticate("cliente", AuthoritiesConstants.CLIENTE);
        Cuenta cuenta = new Cuenta();
        cuenta.setId("cuenta-1");
        when(cuentaRepository.findOneByUserId("cliente")).thenReturn(Optional.of(cuenta));

        Pedido pedidoUno = new Pedido();
        pedidoUno.setId("pedido-1");
        Pedido pedidoDos = new Pedido();
        pedidoDos.setId("pedido-2");
        when(pedidoRepository.findByCuentaId("cuenta-1", Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(pedidoUno, pedidoDos)));

        when(itemPedidoRepository.findByPedidoId("pedido-1")).thenReturn(List.of(itemPedido("item-1", "pedido-1", 2, "120000")));
        when(itemPedidoRepository.findByPedidoId("pedido-2")).thenReturn(List.of(itemPedido("item-2", "pedido-2", 1, "95000")));

        List<ItemPedidoDTO> result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ItemPedidoDTO::getId).containsExactly("item-1", "item-2");
        verify(itemPedidoRepository, org.mockito.Mockito.never()).findAll();
    }

    @Test
    void findAllClienteSinCuentaRetornaListaVacia() {
        authenticate("cliente", AuthoritiesConstants.CLIENTE);
        when(cuentaRepository.findOneByUserId("cliente")).thenReturn(Optional.empty());

        List<ItemPedidoDTO> result = service.findAll();

        assertThat(result).isEmpty();
        verify(pedidoRepository, org.mockito.Mockito.never()).findByCuentaId(any(), any(Pageable.class));
    }

    @Test
    void findOneAdminUsaConsultaConEager() {
        ItemPedido item = itemPedido("item-1", "pedido-1", 2, "120000");
        item.setNombreProducto("Tenis Nike Air");
        when(itemPedidoRepository.findOneWithEagerRelationships("item-1")).thenReturn(Optional.of(item));

        Optional<ItemPedidoDTO> result = service.findOne("item-1");

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().getId()).isEqualTo("item-1");
        assertThat(result.orElseThrow().getNombreProducto()).isEqualTo("Tenis Nike Air");
        assertThat(result.orElseThrow().getPedido()).isNotNull();
        assertThat(result.orElseThrow().getPedido().getId()).isEqualTo("pedido-1");
        verify(itemPedidoRepository).findOneWithEagerRelationships("item-1");
    }

    @Test
    void findOneAdminRetornaVacioCuandoNoExiste() {
        when(itemPedidoRepository.findOneWithEagerRelationships("no-existe")).thenReturn(Optional.empty());

        Optional<ItemPedidoDTO> result = service.findOne("no-existe");

        assertThat(result).isEmpty();
    }

    @Test
    void findOneClienteEncuentraItemDentroDeSusPedidos() {
        authenticate("cliente", AuthoritiesConstants.CLIENTE);
        Cuenta cuenta = new Cuenta();
        cuenta.setId("cuenta-1");
        when(cuentaRepository.findOneByUserId("cliente")).thenReturn(Optional.of(cuenta));

        Pedido pedido = new Pedido();
        pedido.setId("pedido-1");
        when(pedidoRepository.findByCuentaId("cuenta-1", Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(pedido)));

        when(itemPedidoRepository.findByIdAndPedidoId("item-1", "pedido-1")).thenReturn(
            Optional.of(itemPedido("item-1", "pedido-1", 2, "120000"))
        );

        Optional<ItemPedidoDTO> result = service.findOne("item-1");

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().getId()).isEqualTo("item-1");
        assertThat(result.orElseThrow().getPedido().getId()).isEqualTo("pedido-1");
    }

    @Test
    void findOneClienteConItemDeOtroPedidoDevuelveVacio() {
        authenticate("cliente", AuthoritiesConstants.CLIENTE);
        Cuenta cuenta = new Cuenta();
        cuenta.setId("cuenta-1");
        when(cuentaRepository.findOneByUserId("cliente")).thenReturn(Optional.of(cuenta));

        Pedido pedido = new Pedido();
        pedido.setId("pedido-1");
        when(pedidoRepository.findByCuentaId("cuenta-1", Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(pedido)));

        when(itemPedidoRepository.findByIdAndPedidoId("item-ajeno", "pedido-1")).thenReturn(Optional.empty());

        Optional<ItemPedidoDTO> result = service.findOne("item-ajeno");

        assertThat(result).isEmpty();
    }

    @Test
    void findAllWithEagerRelationshipsRetornaPaginaDeDtos() {
        ItemPedido item = itemPedido("item-1", "pedido-1", 2, "120000");
        when(itemPedidoRepository.findAllWithEagerRelationships(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(item)));

        var result = service.findAllWithEagerRelationships(PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent())
            .singleElement()
            .satisfies(dto -> assertThat(dto.getId()).isEqualTo("item-1"));
    }

    @Test
    void deleteEliminaPorId() {
        service.delete("item-1");

        verify(itemPedidoRepository).deleteById("item-1");
    }
}
