package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.domain.enumeration.EstadoPago;
import com.mycompany.knstore.domain.enumeration.EstadoPedido;
import com.mycompany.knstore.domain.enumeration.MetodoPago;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import com.mycompany.knstore.service.ItemPedidoService;
import com.mycompany.knstore.service.PagoService;
import com.mycompany.knstore.service.PedidoService;
import com.mycompany.knstore.service.dto.CheckoutItemDTO;
import com.mycompany.knstore.service.dto.CheckoutRequestDTO;
import com.mycompany.knstore.service.dto.CheckoutResultDTO;
import com.mycompany.knstore.service.dto.CuentaDTO;
import com.mycompany.knstore.service.dto.DireccionDTO;
import com.mycompany.knstore.service.dto.ItemPedidoDTO;
import com.mycompany.knstore.service.dto.PagoDTO;
import com.mycompany.knstore.service.dto.PedidoDTO;
import com.mycompany.knstore.service.dto.ProductoDTO;
import com.mycompany.knstore.service.mapper.PedidoMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.knstore.domain.Pedido}.
 */
@Service
public class PedidoServiceImpl implements PedidoService {

    private static final Logger LOG = LoggerFactory.getLogger(PedidoServiceImpl.class);

    private final PedidoRepository pedidoRepository;

    private final CuentaRepository cuentaRepository;

    private final ItemPedidoService itemPedidoService;

    private final PagoService pagoService;

    private final PedidoMapper pedidoMapper;

    public PedidoServiceImpl(
        PedidoRepository pedidoRepository,
        CuentaRepository cuentaRepository,
        ItemPedidoService itemPedidoService,
        PagoService pagoService,
        PedidoMapper pedidoMapper
    ) {
        this.pedidoRepository = pedidoRepository;
        this.cuentaRepository = cuentaRepository;
        this.itemPedidoService = itemPedidoService;
        this.pagoService = pagoService;
        this.pedidoMapper = pedidoMapper;
    }

    @Override
    @Transactional
    public CheckoutResultDTO checkout(CheckoutRequestDTO request) {
        LOG.debug("Request to checkout : {}", request);

        String cuentaId = getCurrentAccountId().orElseThrow(() ->
            new com.mycompany.knstore.web.rest.errors.BadRequestAlertException("User account required", "pedido", "accountrequired")
        );

        BigDecimal subtotal = request
            .getItems()
            .stream()
            .map(item -> item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        DireccionDTO direccionDTO = new DireccionDTO();
        direccionDTO.setId(request.getDireccionId());
        CuentaDTO cuentaDTO = new CuentaDTO();
        cuentaDTO.setId(cuentaId);

        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setNumeroPedido("ORD-" + Instant.now().toEpochMilli());
        pedidoDTO.setEstado(EstadoPedido.PENDING);
        pedidoDTO.setSubtotal(subtotal);
        pedidoDTO.setCostoEnvio(request.getCostoEnvio());
        pedidoDTO.setTotal(subtotal.add(request.getCostoEnvio()));
        pedidoDTO.setNotasCliente(request.getNotasCliente());
        pedidoDTO.setDireccion(direccionDTO);
        pedidoDTO.setCuenta(cuentaDTO);

        PedidoDTO savedPedido = save(pedidoDTO);

        for (CheckoutItemDTO item : request.getItems()) {
            ItemPedidoDTO itemDTO = new ItemPedidoDTO();
            itemDTO.setCantidad(item.getCantidad());
            itemDTO.setPrecioUnitario(item.getPrecioUnitario());
            itemDTO.setSubtotal(item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())));
            itemDTO.setNombreProducto(item.getNombreProducto());
            itemDTO.setSlugProducto(item.getSlugProducto());
            itemDTO.setMarcaProducto(item.getMarcaProducto());
            itemDTO.setSkuProducto(item.getSkuProducto());
            itemDTO.setColorProducto(item.getColorProducto());
            itemDTO.setTallaProducto(item.getTallaProducto());
            ProductoDTO productoDTO = new ProductoDTO();
            productoDTO.setId(item.getProductoId());
            itemDTO.setPedido(savedPedido);
            itemDTO.setProducto(productoDTO);
            itemPedidoService.save(itemDTO);
        }

        PagoDTO pagoDTO = new PagoDTO();
        pagoDTO.setMetodoPago(MetodoPago.valueOf(request.getMetodoPago()));
        pagoDTO.setEstado(EstadoPago.PENDING);
        pagoDTO.setMonto(savedPedido.getTotal());
        pagoDTO.setPedido(savedPedido);
        pagoService.save(pagoDTO);

        return new CheckoutResultDTO(
            savedPedido.getId(),
            savedPedido.getNumeroPedido(),
            savedPedido.getTotal(),
            savedPedido.getEstado().name()
        );
    }

    @Override
    public PedidoDTO save(PedidoDTO pedidoDTO) {
        LOG.debug("Request to save Pedido : {}", pedidoDTO);
        Pedido pedido = pedidoMapper.toEntity(pedidoDTO);
        pedido = pedidoRepository.save(pedido);
        return pedidoMapper.toDto(pedido);
    }

    @Override
    public PedidoDTO update(PedidoDTO pedidoDTO) {
        LOG.debug("Request to update Pedido : {}", pedidoDTO);
        Pedido pedido = pedidoMapper.toEntity(pedidoDTO);
        pedido = pedidoRepository.save(pedido);
        return pedidoMapper.toDto(pedido);
    }

    @Override
    public Optional<PedidoDTO> partialUpdate(PedidoDTO pedidoDTO) {
        LOG.debug("Request to partially update Pedido : {}", pedidoDTO);

        return pedidoRepository
            .findById(pedidoDTO.getId())
            .map(existingPedido -> {
                pedidoMapper.partialUpdate(existingPedido, pedidoDTO);

                return existingPedido;
            })
            .map(pedidoRepository::save)
            .map(pedidoMapper::toDto);
    }

    @Override
    public Page<PedidoDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Pedidos");
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return getCurrentAccountId()
                .map(cuentaId -> pedidoRepository.findByCuentaId(cuentaId, pageable).map(pedidoMapper::toDto))
                .orElse(Page.empty(pageable));
        }
        return pedidoRepository.findAll(pageable).map(pedidoMapper::toDto);
    }

    /**
     *  Get all the pedidos where Envio is {@code null}.
     *  @return the list of entities.
     */

    public List<PedidoDTO> findAllWhereEnvioIsNull() {
        LOG.debug("Request to get all pedidos where Envio is null");
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return getCurrentAccountId()
                .map(cuentaId ->
                    pedidoRepository
                        .findByCuentaIdAndEnvioIsNull(cuentaId)
                        .stream()
                        .map(pedidoMapper::toDto)
                        .collect(Collectors.toCollection(LinkedList::new))
                )
                .orElseGet(LinkedList::new);
        }
        return StreamSupport.stream(pedidoRepository.findAll().spliterator(), false)
            .filter(pedido -> pedido.getEnvio() == null)
            .map(pedidoMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public Optional<PedidoDTO> findOne(String id) {
        LOG.debug("Request to get Pedido : {}", id);
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return getCurrentAccountId()
                .flatMap(cuentaId -> pedidoRepository.findByIdAndCuentaId(id, cuentaId))
                .map(pedidoMapper::toDto);
        }
        return pedidoRepository.findById(id).map(pedidoMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete Pedido : {}", id);
        pedidoRepository.deleteById(id);
    }

    private Optional<String> getCurrentAccountId() {
        return SecurityUtils.getCurrentUserId()
            .flatMap(cuentaRepository::findOneByUserId)
            .map(cuenta -> cuenta.getId());
    }
}
